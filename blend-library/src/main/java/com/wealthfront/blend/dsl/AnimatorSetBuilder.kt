package com.wealthfront.blend.dsl

import android.animation.Animator
import com.wealthfront.blend.animator.BlendableAnimator
import com.wealthfront.blend.builder.AnimatorSetWrapper
import javax.annotation.CheckReturnValue

/**
 * The return value of a [com.wealthfront.blend.Blend.invoke] block.
 *
 * Provides methods to chain more transitions ([AnimatorSetBuilder]) and to start the animation or prepare an animator
 * for later starting ([AnimationStarter]).
 */
@CheckReturnValue
class AnimatorSetBuilder(
  override val animatorSet: AnimatorSetWrapper,
  private val createNewBlendableAnimator: () -> BlendableAnimator,
  private val createNewAnimatorSet: (Animator) -> AnimatorSetWrapper
) : SetBuilder<AnimatorSetBuilder>, AnimationStarter {

  override fun then(action: AnimatorBuilder.() -> Unit): AnimatorSetBuilder {
    val newAnimator = createNewBlendableAnimator()
    val newAnimatorSet = if (animatorSet.childAnimators.size > 1) {
      createNewAnimatorSet(animatorSet.asAnimator())
    } else {
      animatorSet
    }
    val currentAnimator = if (animatorSet.childAnimators.size > 1) {
      animatorSet.asAnimator()
    } else {
      animatorSet.childAnimators.first()
    }
    newAnimatorSet.addSequential(currentAnimator, newAnimator)
    AnimatorBuilder(newAnimator, newAnimatorSet, createNewBlendableAnimator).apply(action)
    return AnimatorSetBuilder(
        newAnimatorSet,
        createNewBlendableAnimator,
        createNewAnimatorSet)
  }

  override fun with(action: AnimatorBuilder.() -> Unit): AnimatorSetBuilder {
    val newAnimator = createNewBlendableAnimator()
    val newAnimatorSet = if (animatorSet.childAnimators.size > 1) {
      createNewAnimatorSet(animatorSet.asAnimator())
    } else {
      animatorSet
    }
    val currentAnimator = if (animatorSet.childAnimators.size > 1) {
      animatorSet.asAnimator()
    } else {
      animatorSet.childAnimators.first()
    }
    newAnimatorSet.addSimultaneous(currentAnimator, newAnimator)
    AnimatorBuilder(newAnimator, newAnimatorSet, createNewBlendableAnimator).apply(action)
    return AnimatorSetBuilder(
        newAnimatorSet,
        createNewBlendableAnimator,
        createNewAnimatorSet)
  }
}
