package com.wealthfront.blend.animator

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import com.wealthfront.blend.properties.AdditiveProperty
import java.util.ArrayList

/**
 * An [Animator] that blends concurrent animations on the same properties on the same subject.
 *
 * It runs the [animations] described by [SinglePropertyAnimation] objects. Add animations with [addAnimation] and
 * listeners with `doOn*` methods.
 *
 * It wraps a [ValueAnimator] ([innerAnimator]) and delegates all [Animator] methods to it, with some redirection to
 * ensure proper adherence to e.g. listener contracts.
 *
 * For proper functioning in sets, you must call [commitFutureValuesIfNotCommitted] when the set of animations is
 * started, even if this animation is delayed. If not, the end state of the set of animations becomes dependent on
 * when the individual animators start, which can be unpredictable when they're user-initiated.
 */
open class BlendableAnimator : Animator() {

  /**
   * The wrapped [ValueAnimator].
   */
  @VisibleForTesting var innerAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)!!
  /**
   * The [SinglePropertyAnimation]s to play in this animator.
   */
  @VisibleForTesting val animations: MutableList<SinglePropertyAnimation<*>> = mutableListOf()
  /**
   * The actions to run on every frame, e.g. to invalidate the subjects of the [animations].
   */
  @VisibleForTesting val everyFrameActions: MutableList<() -> Unit> = mutableListOf()
  /**
   * The actions to run before this animator starts.
   */
  @VisibleForTesting val beforeStartActions: MutableList<() -> Unit> = mutableListOf()
  private var valuesCommitted = false

  val allAnimations: List<SinglePropertyAnimation<*>> get() = animations.toList()

  open var repeatCount: Int
    get() = innerAnimator.repeatCount
    set(count) {
      innerAnimator.repeatCount = count
    }

  open var repeatMode: Int
    get() = innerAnimator.repeatMode
    set(mode) {
      innerAnimator.repeatMode = mode
    }

  /**
   * Add a [SinglePropertyAnimation] with the given arguments.
   *
   * @param subject The subject of the animation
   * @param property The property to animate
   * @param targetValue The value to animate [property] to
   */
  open fun <Subject> addAnimation(
    subject: Subject,
    property: AdditiveProperty<Subject>,
    targetValue: Float
  ) {
    addAnimation(SinglePropertyAnimation(subject, property, targetValue))
  }

  /**
   * Add [animation] to the list of animations to run.
   */
  open fun addAnimation(animation: SinglePropertyAnimation<*>) {
    animations += animation
  }

  /**
   * Add [animations] to the list of animations to run.
   */
  open fun addAnimations(animations: Collection<SinglePropertyAnimation<*>>) {
    this.animations += animations
  }

  /**
   * Remove [animations] from the list of animations to run.
   */
  open fun removeAnimations(animations: Collection<SinglePropertyAnimation<*>>) {
    this.animations.removeAll(animations)
  }

  /**
   * Commit the future values of all [animations] to the [com.wealthfront.blend.properties.AnimationData] objects
   * described by their properties.
   *
   * These future values are necessary for any animations started after this animator to blend properly.
   */
  open fun commitFutureValuesIfNotCommitted() {
    if (!valuesCommitted) {
      beforeStartActions.forEach { it() }
      animations.forEach { it.setUpOnAnimationCommitted(this) }
    }
    valuesCommitted = true
  }

  /**
   * Mark all [animations] as part of a fully-committed set of animations.
   *
   * This step is necessary for individual animations in a set to not remove each other when being committing.
   */
  open fun markAnimationsAsFullyCommitted() {
    animations.forEach { it.isPartOfAFullyCommittedSet = true }
  }

  override fun start() {
    if (isRunning) {
      throw IllegalStateException("Cannot start an already-running animation")
    }
    innerAnimator.addUpdateListener { animator ->
      animations.forEach { it.applyChanges(animator.animatedFraction) }
      everyFrameActions.forEach { it() }
    }
    innerAnimator.addListener(object : AnimatorListenerAdapter() {
      override fun onAnimationEnd(animation: Animator?) {
        animations.forEach { it.runEndActions() }
        valuesCommitted = false
      }
    })

    if (repeatCount > 0) {
      cancelAnimatorOnViewsDetached()
    }
    commitFutureValuesIfNotCommitted()
    innerAnimator.start()
    markAnimationsAsFullyCommitted()
  }

  private fun cancelAnimatorOnViewsDetached() {
    animations
        .forEach { animation ->
          (animation.subject as? View)?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
              cancel()
            }

            override fun onViewAttachedToWindow(v: View?) { }
          })
        }
  }

  open fun doOnFinishedUnlessLastAnimationInterrupted(endAction: () -> Unit) {
    animations.last().interruptibleEndActions.add(endAction)
  }

  open fun doOnFinishedEvenIfInterrupted(endAction: () -> Unit) {
    innerAnimator.addListener(object : AnimatorListenerAdapter() {
      override fun onAnimationEnd(animation: Animator?) {
        endAction()
      }
    })
  }

  open fun doEveryFrame(action: () -> Unit) {
    everyFrameActions += action
  }

  open fun doBeforeStart(action: () -> Unit) {
    beforeStartActions += action
  }

  open fun doOnStart(startAction: () -> Unit) {
    innerAnimator.addListener(object : AnimatorListenerAdapter() {
      override fun onAnimationStart(animation: Animator?) {
        startAction()
      }
    })
  }

  override fun isRunning(): Boolean = innerAnimator.isRunning

  override fun getDuration(): Long = innerAnimator.duration

  override fun getStartDelay(): Long = innerAnimator.startDelay

  override fun setStartDelay(startDelay: Long): Unit = innerAnimator.setStartDelay(startDelay)

  override fun setInterpolator(value: TimeInterpolator?): Unit = innerAnimator.setInterpolator(value)

  override fun setDuration(duration: Long): Animator = innerAnimator.setDuration(duration)

  override fun addPauseListener(listener: AnimatorPauseListener?) = innerAnimator.addPauseListener(listener)

  override fun setTarget(target: Any?) {
    throw IllegalStateException("BlendableAnimator doesn't support setting an individual target")
  }

  override fun addListener(listener: AnimatorListener) =
      innerAnimator.addListener(ListenerWrapper(listener, this))

  override fun getListeners(): ArrayList<AnimatorListener>? = innerAnimator.listeners

  override fun cancel() = innerAnimator.cancel()

  override fun removeListener(listener: AnimatorListener) =
      innerAnimator.removeListener(ListenerWrapper(listener, this))

  override fun getInterpolator(): TimeInterpolator = innerAnimator.interpolator

  override fun clone(): Animator =
      throw UnsupportedOperationException("Cloning blendable animators isn't supported yet")

  open fun copyWithoutAnimations(): BlendableAnimator = BlendableAnimator().also { newAnimator ->
    newAnimator.interpolator = interpolator
    newAnimator.duration = duration
  }

  override fun resume() = innerAnimator.resume()

  override fun isStarted(): Boolean = innerAnimator.isStarted

  override fun removePauseListener(listener: AnimatorPauseListener?) = innerAnimator.removePauseListener(listener)

  override fun isPaused(): Boolean = innerAnimator.isPaused

  override fun pause() = innerAnimator.pause()

  override fun end() = innerAnimator.end()

  override fun setupStartValues() = innerAnimator.setupStartValues()

  override fun removeAllListeners() = innerAnimator.removeAllListeners()

  @RequiresApi(Build.VERSION_CODES.O)
  override fun getTotalDuration(): Long = innerAnimator.totalDuration

  override fun setupEndValues() = innerAnimator.setupEndValues()
}

/**
 * A wrapper for an [AnimatorListener] that allows us to pass our [BlendableAnimator] as the argument instead of it's
 * [BlendableAnimator.innerAnimator].
 */
private data class ListenerWrapper(val innerListener: AnimatorListener, val pretendAnimator: Animator) : AnimatorListener {
  override fun onAnimationRepeat(animation: Animator?) {
    innerListener.onAnimationRepeat(pretendAnimator)
  }

  override fun onAnimationEnd(animation: Animator?) {
    innerListener.onAnimationEnd(pretendAnimator)
  }

  override fun onAnimationCancel(animation: Animator?) {
    innerListener.onAnimationCancel(pretendAnimator)
  }

  override fun onAnimationStart(animation: Animator?) {
    innerListener.onAnimationStart(pretendAnimator)
  }
}

