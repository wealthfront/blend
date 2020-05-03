package com.wealthfront.blend.properties

import android.view.View
import androidx.annotation.IdRes
import com.wealthfront.blend.animator.SinglePropertyAnimation
import kotlin.math.roundToInt

/**
 * A property of [Subject] that can blend.
 *
 * To properly blend animations, it includes methods like [getFutureValue], [addInterruptableEndActions], and
 * [getAnimationData].
 *
 * We store data on queued animations ([AnimationData]) on each subject, ideally. This systematically prevents leaks
 * of references to [Subject]s. The mechanism for storing is described by the implementation of [getAnimationData]
 */
interface AdditiveProperty<in Subject> {

  /**
   * An id resource that distinguishes this property from others. **Must** be unique, otherwise animations
   * will break.
   */
  @get:IdRes val id: Int

  /**
   * Set the value of this property.
   */
  fun setValue(subject: Subject, value: Float)

  /**
   * Add the given value to the current value of this property.
   */
  fun addValue(subject: Subject, delta: Float) = setValue(subject, getCurrentValue(subject) + delta)

  /**
   * Describe how to interpolate the inputs for this property. For example, colors will be interpolated differently than
   * regular integers.
   */
  fun interpolate(startValue: Float, endValue: Float, timeFraction: Float, subject: Subject) =
      linearlyInterpolate(startValue, endValue, timeFraction)

  /**
   * Get the current value of this property.
   */
  fun getCurrentValue(subject: Subject): Float

  /**
   * Get the value of this property as if all running animations have finished.
   */
  fun getFutureValue(subject: Subject): Float =
      getAnimationData(subject).futureValue ?: getCurrentValue(subject)

  /**
   * Perform any setup tasks when an animation on this property starts.
   */
  fun setUpOnAnimationQueued(subject: Subject) { }

  /**
   * Add a queued animation to the [subject]'s [AnimationData] for this property.
   */
  fun addQueuedAnimation(subject: Subject, animation: SinglePropertyAnimation<*>) =
      getAnimationData(subject).addQueuedAnimation(animation)

  /**
   * Remove a queued animation from the [subject]'s [AnimationData] for this property.
   */
  fun removeQueuedAnimation(subject: Subject, animation: SinglePropertyAnimation<*>) =
      getAnimationData(subject).removeQueuedAnimation(animation)

  /**
   * Add [endActions] to the [subject]'s [AnimationData] for this property so they can be cancelled if another
   * animation of this property is started on [subject].
   */
  fun addInterruptableEndActions(subject: Subject, vararg endActions: () -> Unit) =
      getAnimationData(subject).addInterruptableEndActions(*endActions)

  /**
   * Run the end actions added in [addInterruptableEndActions] if another animation of this property hasn't been started
   * on [subject].
   */
  fun runEndActions(subject: Subject, animation: SinglePropertyAnimation<*>) {
    val animationData = getAnimationData(subject)
    if (animation == animationData.queuedAnimations.lastOrNull()) {
      animationData.interruptableEndActions.forEach { it() }
      animationData.interruptableEndActions.clear()
    }
  }

  /**
   * Get the animation data associated with this property on the [subject].
   */
  fun getAnimationData(subject: Subject): AnimationData
}

/**
 * An [AdditiveProperty] specifically designed for ints.
 */
interface AdditiveIntProperty<in Subject> : AdditiveProperty<Subject> {

  override fun interpolate(startValue: Float, endValue: Float, timeFraction: Float, subject: Subject): Float {
    return linearlyInterpolate(
      startValue.roundToInt(),
      endValue.roundToInt(),
      timeFraction)
      .toFloat()
  }
}

/**
 * An [AdditiveProperty] specifically designed for views. Handles storing running animations for you.
 */
interface AdditiveViewProperty<in Subject : View> : AdditiveProperty<Subject> {

  override fun getAnimationData(subject: Subject): AnimationData = subject.getAnimationData(id)
}
