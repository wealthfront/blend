package com.wealthfront.blend.dsl

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.view.animation.AnticipateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.wealthfront.blend.EmphasizedInterpolator
import com.wealthfront.blend.animator.BlendableAnimator
import com.wealthfront.blend.builder.AnimatorSetWrapper
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.MILLISECONDS

/**
 * Builder interface that describes how to [start] animators or [prepare] them for later use.
 */
@BuilderMarker
interface AnimationStarter {
  val animatorSet: AnimatorSetWrapper

  /**
   * Start the current set of animators.
   */
  fun start() {
    prepare().start()
  }

  /**
   * Prepare the current set of animators as an [Animator] for later use.
   */
  fun prepare(): Animator {
    return animatorSet.asAnimator().apply {
      addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
          val animators = animatorSet.childAnimators.mapNotNull { it as? BlendableAnimator }
          animators.forEach { it.commitFutureValuesIfNotCommitted() }
          animators.forEach { it.markAnimationsAsFullyCommitted() }
        }
      })
    }
  }
}


/**
 * Builder interface that describes chaining animators.
 */
@BuilderMarker
interface SetBuilder<Self : SetBuilder<Self>> {

  /**
   * Start the animator described in the [action] after the current set of animators.
   */
  fun then(action: AnimatorBuilder.() -> Unit): Self

  /**
   * Start the animator described in the [action] at the beginning of the current set of animators.
   */
  fun with(action: AnimatorBuilder.() -> Unit): Self
}

/**
 * Builder interface that describes changing timing values of the current animator, like [duration] and [ease].
 */
@BuilderMarker
interface TimingBuilder {
  val currentAnimator: BlendableAnimator

  /**
   * Change the duration of the current animator to the given [time] * [timeUnit].
   */
  fun duration(time: Long, timeUnit: TimeUnit = MILLISECONDS) {
    currentAnimator.duration = timeUnit.toMillis(time)
  }

  /**
   * Change the duration of the current animator to 0.
   */
  fun immediate() = duration(0, MILLISECONDS)

  /**
   * Repeat the current animator [count] times with the given repeat [mode]
   */
  fun repeat(count: Int, mode: Int) {
    currentAnimator.repeatCount = count
    currentAnimator.repeatMode = mode
  }

  /**
   * Ease the current animator using [interpolator].
   */
  fun ease(interpolator: TimeInterpolator) {
    currentAnimator.interpolator = interpolator
  }

  /**
   * Ease the current animator using [FastOutSlowInInterpolator].
   */
  fun defaultEase() = ease(FastOutSlowInInterpolator())
  /**
   * Ease the current animator using [FastOutLinearInInterpolator].
   */
  fun accelerate() = ease(FastOutLinearInInterpolator())
  /**
   * Ease the current animator using [LinearOutSlowInInterpolator].
   */
  fun decelerate() = ease(LinearOutSlowInInterpolator())
  /**
   * Ease the current animator using [EmphasizedInterpolator].
   */
  fun emphasizeEase() = ease(EmphasizedInterpolator())
  /**
   * Ease the current animator using [OvershootInterpolator].
   */
  fun overshoot() = ease(OvershootInterpolator())
  /**
   * Ease the current animator using [AnticipateInterpolator].
   */
  fun anticipate() = ease(AnticipateInterpolator())

  /**
   * Add a startDelay to the current animator of [delay] * [timeUnit].
   */
  fun startDelay(delay: Long, timeUnit: TimeUnit = MILLISECONDS) {
    currentAnimator.startDelay = timeUnit.toMillis(delay)
  }
}

/**
 * Builder interface that describes adding listeners to the current animator.
 */
@BuilderMarker
interface ListenerBuilder {
  val currentAnimator: BlendableAnimator

  /**
   * Perform [endAction] when this animator finishes, unless a separate animation is started on the same property as
   * the last animation described.
   *
   * For example, say we call the following:
   * ```
   * blend {
   *   target(view).animations {
   *     height(100)
   *   }
   *   doOnFinishedUnlessLastAnimationInterrupted {
   *     view.height = WRAP_CONTENT
   *   }
   * }.start()
   * ```
   *
   * If we then interrupt it by animating `height` to something else, the listener that sets it to `WRAP_CONTENT`
   * will be cancelled.
   */
  fun doOnFinishedUnlessLastAnimationInterrupted(endAction: () -> Unit) {
    currentAnimator.doOnFinishedUnlessLastAnimationInterrupted(endAction)
  }

  /**
   * Perform [endAction] when this animator finishes, even if other animations start on the same subject.
   */
  fun doOnFinishedEvenIfInterrupted(endAction: () -> Unit) {
    currentAnimator.doOnFinishedEvenIfInterrupted(endAction)
  }

  /**
   * Add a [startAction] that runs when the current animator starts.
   */
  fun doOnStart(startAction: () -> Unit) {
    currentAnimator.doOnStart(startAction)
  }

  /**
   * Add an [action] that runs on every frame, e.g. to invalidate the current subjects.
   */
  fun doEveryFrame(action: () -> Unit) {
    currentAnimator.doEveryFrame(action)
  }
}
