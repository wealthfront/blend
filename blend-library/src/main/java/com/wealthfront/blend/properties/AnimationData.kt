package com.wealthfront.blend.properties

import com.wealthfront.blend.animator.SinglePropertyAnimation
import kotlin.math.roundToInt

/**
 * A container for storing data about queued animations to facilitate blending.
 *
 * Associated with a specific subject and a property.
 */
class AnimationData {

  /**
   * All animations currently queued to run on the associated subject for the associated property. These animations
   * may or may not be currently running, however.
   */
  val queuedAnimations: MutableList<SinglePropertyAnimation<*>> = mutableListOf()
  /**
   * End actions that will be cancelled if another animation is started for the associated subject.property.
   */
  val interruptableEndActions: MutableList<() -> Unit> = mutableListOf()
  var isLatestAnimationDone = false

  /**
   * Get the value of this property as if all queued animations have finished.
   */
  val futureValue: Float?
    get() = queuedAnimations.lastOrNull { it.isStarted }?.targetValue

  /**
   * Register a new animation that has started. This cancels any [interruptableEndActions] that we have, since a new
   * animation has started.
   *
   * This also cancels all non-started [queuedAnimations], in order to avoid animating to any unexpected values.
   * See [this github issue](https://github.com/wealthfront/blend/issues/4) for more information.
   */
  fun addQueuedAnimation(animation: SinglePropertyAnimation<*>) {
    if (isLatestAnimationDone) {
      queuedAnimations -= queuedAnimations.last()
    }
    isLatestAnimationDone = false
    queuedAnimations.filter {
      !it.isStarted && it.isPartOfAFullyQueuedSet
    }.forEach {
      it.markCancelled()
      queuedAnimations.remove(it)
    }
    queuedAnimations += animation
    interruptableEndActions.clear()
  }

  /**
   * Remove a queued animation because it has either finished or been cancelled.
   */
  fun removeQueuedAnimation(animation: SinglePropertyAnimation<*>) {
    animation.markCancelled()
    if (queuedAnimations.lastOrNull() == animation && queuedAnimations.size > 1) {
      isLatestAnimationDone = true
    } else {
      queuedAnimations -= animation
      if (isLatestAnimationDone && queuedAnimations.size == 1) {
        queuedAnimations.clear()
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
