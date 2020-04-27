package com.wealthfront.blend.mock

import android.animation.Animator
import android.animation.TimeInterpolator
import com.wealthfront.blend.builder.AnimatorSetWrapper

class ImmediateAnimatorSetWrapper(rootAnimator: Animator) : AnimatorSetWrapper(rootAnimator) {

  override fun start() {
    animatorSet.childAnimations.forEach { it.start() }
  }

  override fun asAnimator(): Animator = object : Animator() {
    override fun start() {
      this@ImmediateAnimatorSetWrapper.start()
    }

    override fun isRunning(): Boolean = false

    override fun getDuration(): Long = 0

    override fun getStartDelay(): Long = 0

    override fun setStartDelay(startDelay: Long) { }

    override fun setInterpolator(value: TimeInterpolator?) { }

    override fun setDuration(duration: Long): Animator = this
  }
}
