package com.wealthfront.blend.properties

import com.wealthfront.blend.animator.SinglePropertyAnimation
import kotlin.math.roundToInt

/**
 * A container for storing data about running animations to facilitate blending.
 *
 * Associated with a specific subject and a property.
 */
class AnimationData {

  /**
   * All animations currently running on the associated subject for the associated property.
   */
  val runningAnimations: MutableList<SinglePropertyAnimation<*>> = mutableListOf()
  /**
   * End actions that will be cancelled if another animation is started for the associated subject.property.
   */
  val interruptableEndActions: MutableList<() -> Unit> = mutableListOf()
  var isLatestAnimationDone = false

  /**
   * Get the value of this property as if all running animations have finished.
   */
  val futureValue: Float?
    get() = runningAnimations.lastOrNull()?.targetValue

  /**
   * Register a new animation that has started.
   *
   * This also cancels any [interruptableEndActions] that we have, since a new animation has started.
   */
  fun addRunningAnimation(animation: SinglePropertyAnimation<*>) {
    if (isLatestAnimationDone) {
      runningAnimations -= runningAnimations.last()
    }
    isLatestAnimationDone = false
    runningAnimations += animation
    interruptableEndActions.clear()
  }

  /**
   * Remove a running animation because it has either finished or been cancelled.
   */
  fun removeRunningAnimation(animation: SinglePropertyAnimation<*>) {
    if (runningAnimations.lastOrNull() == animation && runningAnimations.size > 1) {
      isLatestAnimationDone = true
    } else {
      runningAnimations -= animation
      if (isLatestAnimationDone && runningAnimations.size == 1) {
        runningAnimations.clear()
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
