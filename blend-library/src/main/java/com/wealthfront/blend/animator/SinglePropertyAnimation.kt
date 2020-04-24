package com.wealthfront.blend.animator

import androidx.annotation.VisibleForTesting
import com.wealthfront.blend.properties.AdditiveProperty

/**
 * A description of an animation of a single [property] of a single [subject] to a final value of [targetValue].
 */
class SinglePropertyAnimation<Subject>(
  val subject: Subject,
  val property: AdditiveProperty<Subject>,
  val targetValue: Float
) {

  /**
   * End actions added by [BlendableAnimator.doOnFinishedUnlessLastAnimationInterrupted] that should be cancelled if
   * another animation on [subject].[property] is started.
   */
  val interruptibleEndActions: MutableList<() -> Unit> = mutableListOf()
  /**
   * The value of [property] at the beginning of the animation. Calculated when the animator running this animation is
   * started.
   */
  private var startValue: Float = 0f
  /**
   * The value of [property] at the last frame computed. Initialized to [startValue] on the first frame of animation.
   */
  private var previousValue: Float = 0f
  /**
   * The animator running this animation
   */
  var animator: BlendableAnimator? = null
    @VisibleForTesting internal set
  /**
   * Whether this animation has started running. Note that it can be committed (i.e. queued) and not started.
   */
  val isStarted: Boolean = animator?.isStarted ?: false
  /**
   * Whether the set that this animation belongs to is done committing all of its animations. Useful for figuring out
   * when to cancel unstarted (but committed) animations.
   */
  var isPartOfAFullyCommittedSet: Boolean = false

  /**
   * Set up the starting values for this animation and commit the [targetValue] as [property]'s future value.
   */
  fun setUpOnAnimationCommitted(animator: BlendableAnimator) {
    this.animator = animator
    property.setUpOnAnimationCommitted(subject)
    startValue = property.getFutureValue(subject)
    previousValue = startValue
    property.addCommittedAnimation(subject, this)
    property.addInterruptableEndActions(subject, *interruptibleEndActions.toTypedArray())
  }

  /**
   * Apply the changes between the last frame and this one. Run on each frame.
   */
  fun applyChanges(animatedFraction: Float) {
    val newValue = property.interpolate(startValue, targetValue, animatedFraction, subject)
    property.addValue(subject, newValue - previousValue)
    previousValue = newValue
  }

  fun markCancelled() {
    startValue = targetValue
    previousValue = targetValue
  }

  /**
   * Run the end actions added by [BlendableAnimator.doOnFinishedUnlessLastAnimationInterrupted], if we haven't been
   * interrupted by another animation on [subject].[property].
   */
  fun runEndActions() {
    property.runEndActions(subject, this)
    property.removeCommittedAnimation(subject, this)
  }
}