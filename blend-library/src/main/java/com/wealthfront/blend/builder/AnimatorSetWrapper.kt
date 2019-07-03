package com.wealthfront.blend.builder

import android.animation.Animator
import android.animation.AnimatorSet

/**
 * A wrapper around [AnimatorSet] that allows us to mock it in tests and run animations instantly.
 */
open class AnimatorSetWrapper(rootAnimator: Animator) {

  open val animatorSet = AnimatorSet().apply { play(rootAnimator) }

  open val childAnimators: List<Animator> get() = animatorSet.childAnimations

  open fun start() {
    animatorSet.start()
  }

  open fun addSequential(first: Animator, second: Animator) {
    animatorSet.playSequentially(first, second)
  }

  open fun addSimultaneous(animatorOne: Animator, animatorTwo: Animator) {
    animatorSet.playTogether(animatorOne, animatorTwo)
  }

  open fun asAnimator(): Animator = animatorSet
}
