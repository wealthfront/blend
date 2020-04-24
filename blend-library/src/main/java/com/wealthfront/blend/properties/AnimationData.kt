package com.wealthfront.blend.properties

import com.wealthfront.blend.animator.SinglePropertyAnimation
import kotlin.math.roundToInt

/**
 * A container for storing data about committed animations to facilitate blending.
 *
 * Associated with a specific subject and a property.
 */
class AnimationData {

  /**
   * All animations currently committed to run on the associated subject for the associated property. These animations
   * may or may not be currently running, however.
   */
  val committedAnimations: MutableList<SinglePropertyAnimation<*>> = mutableListOf()
  /**
   * End actions that will be cancelled if another animation is started for the associated subject.property.
   */
  val interruptableEndActions: MutableList<() -> Unit> = mutableListOf()
  var isLatestAnimationDone = false

  /**
   * Get the value of this property as if all committed animations have finished.
   */
  val futureValue: Float?
    get() = committedAnimations.lastOrNull()?.targetValue

  /**
   * Register a new animation that has started. This cancels any [interruptableEndActions] that we have, since a new
   * animation has started.
   *
   * This also cancels all non-started [committedAnimations], in order to avoid animating to any unexpected values.
   * See [this github issue](https://github.com/wealthfront/blend/issues/4) for more information.
   */
  fun addCommittedAnimation(animation: SinglePropertyAnimation<*>) {
    if (isLatestAnimationDone) {
      committedAnimations -= committedAnimations.last()
    }
    isLatestAnimationDone = false
    committedAnimations.removeAll {
      !it.isStarted && it.isPartOfAFullyCommittedSet
    }
    committedAnimations += animation
    interruptableEndActions.clear()
  }

  /**
   * Remove a committed animation because it has either finished or been cancelled.
   */
  fun removeCommittedAnimation(animation: SinglePropertyAnimation<*>) {
    animation.markCancelled()
    if (committedAnimations.lastOrNull() == animation && committedAnimations.size > 1) {
      isLatestAnimationDone = true
    } else {
      committedAnimations -= animation
      if (isLatestAnimationDone && committedAnimations.size == 1) {
        committedAnimations.clear()
        isLatestAnimationDone = false
      }
    }
  }

  /**
   * Add [endActions] to the list of [interruptableEndActions] to be cancelled if another animation is started for the
   * associated subject.property.
   */
  fun addInterruptableEndActions(vararg endActions: () -> Unit) {
    interruptableEndActions += endActions
  }
}

internal fun linearlyInterpolate(start: Float, end: Float, timeFraction: Float): Float {
  return start + (end - start) * timeFraction
}

internal fun linearlyInterpolate(start: Int, end: Int, timeFraction: Float): Int {
  return (start + (end - start) * timeFraction).roundToInt()
}
