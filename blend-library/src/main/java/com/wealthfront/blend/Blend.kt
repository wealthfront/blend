package com.wealthfront.blend

import android.animation.Animator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.view.View
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.wealthfront.blend.animator.BlendableAnimator
import com.wealthfront.blend.builder.AnimatorSetWrapper
import com.wealthfront.blend.dsl.AnimatorBuilder
import com.wealthfront.blend.dsl.AnimatorSetBuilder
import com.wealthfront.blend.properties.AdditiveViewProperties
import javax.annotation.CheckReturnValue

const val ALPHA_FULL = 1f
const val ALPHA_TRANSPARENT = 0f
const val ANIM_DURATION_DEFAULT_MS = 300L
const val ANIM_DURATION_LONG_MS = 500L
const val ANIM_DURATION_SHORT_MS = 200L
const val ANIM_STAGGER_DEFAULT_MS = 50L

/**
 * The entry point into the Blend DSL.
 *
 * Provides canned animations for common situations, as well as overridable methods [createAnimatorSet] and
 * [createBlendableAnimator] to allow for updated defaults and mocking in tests.
 */
@CheckReturnValue
open class Blend {

  /**
   * The entry point to creating an animation.
   *
   * See [AnimatorBuilder] for animation customization options accessible from the passed-in block, and
   * [AnimatorSetBuilder] for chaining logic
   */
  operator fun invoke(action: AnimatorBuilder.() -> Unit): AnimatorSetBuilder {
    val animator = createBlendableAnimator()
    val animatorSet = createAnimatorSet(animator)
    AnimatorBuilder(animator, animatorSet, ::createBlendableAnimator).apply(action)
    return AnimatorSetBuilder(animatorSet, ::createBlendableAnimator, ::createAnimatorSet)
  }

  /**
   * Start a canned infinite pulse animation.
   */
  fun pulse(vararg views: View) {
    val alphaMax = 0.7f
    val alphaMin = 0.4f

    this {
      target(*views).animations {
        alpha(alphaMin)
      }
    }.then {
      repeat(count = INFINITE, mode = REVERSE)
      target(*views).animations {
        alpha(alphaMax)
      }
    }.start()
  }

  /**
   * Start a canned fade animation.
   */
  fun fadeOutThenFadeIn(outView: View, inView: View) {
    fadeOutThenFadeIn(listOf(outView), listOf(inView))
  }

  /**
   * Start a canned fade animation.
   */
  fun fadeOutThenFadeIn(outViews: List<View>, inViews: List<View>) {
    this {
      target(outViews).animations { fadeOut() }
    }.then {
      target(inViews).animations { fadeIn() }
    }.start()
  }

  /**
   * Stop an infinite pulse and any other alpha animations running on the given targets.
   */
  open fun stopPulsing(vararg views: View) {
    views
        .flatMap { view -> AdditiveViewProperties.ALPHA.getAnimationData(view).committedAnimations }
        .mapNotNull { singlePropertyAnimation -> singlePropertyAnimation.animator }
        .filter { animator -> animator.repeatCount != 0 }
        .toSet()
        .forEach { animator -> animator.cancel() }
    this {
      target(*views).animations { alpha(ALPHA_FULL) }
    }.start()
  }

  /**
   * Override this to create a custom [AnimatorSetWrapper], e.g. for testing.
   */
  protected open fun createAnimatorSet(animator: Animator): AnimatorSetWrapper {
    val animatorSet = AnimatorSetWrapper(animator)
    return animatorSet
  }

  /**
   * Override this to create a custom [BlendableAnimator] with any custom default values.
   */
  protected open fun createBlendableAnimator(): BlendableAnimator = BlendableAnimator().apply {
    interpolator = FastOutSlowInInterpolator()
    duration = ANIM_DURATION_DEFAULT_MS
  }
}
