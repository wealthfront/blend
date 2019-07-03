package com.wealthfront.blend.animator

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
    private set

  /**
   * Set up the starting values for this animation and commit the [targetValue] as [property]'s future value.
   */
  fun setUpOnAnimationStart(animator: BlendableAnimator) {
    this.animator = animator
    property.setUpOnAnimationStart(subject)
    startValue = property.getFutureValue(subject)
    previousValue = startValue
    property.addRunningAnimation(subject, this)
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

  /**
   * Run the end actions added by [BlendableAnimator.doOnFinishedUnlessLastAnimationInterrupted], if we haven't been
   * interrupted by another animation on [subject].[property].
   */
  fun runEndActions() {
    property.runEndActions(subject, this)
    property.removeRunningAnimator(subject, this)
  }
}