package com.wealthfront.blend.mock

import com.wealthfront.blend.animator.BlendableAnimator

class ImmediateBlendableAnimator : BlendableAnimator() {

  override fun start() {
    queueAnimationsIfNotAlreadyQueued()
    innerAnimator.listeners?.forEach { it.onAnimationStart(innerAnimator) }
    animations.forEach { it.applyChanges(1f) }
    everyFrameActions.forEach { it() }
    animations.forEach { it.runEndActions() }
    innerAnimator.listeners?.forEach {
      it.onAnimationEnd(innerAnimator)
    }
  }

  override fun copyWithoutAnimations() = ImmediateBlendableAnimator()
}
