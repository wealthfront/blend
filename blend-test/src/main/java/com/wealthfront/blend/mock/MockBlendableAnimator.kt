package com.wealthfront.blend.mock

import android.animation.ValueAnimator.INFINITE
import com.wealthfront.blend.animator.BlendableAnimator
import com.wealthfront.blend.animator.SinglePropertyAnimation
import com.wealthfront.blend.builder.HoldHeightConstantAction
import com.wealthfront.blend.builder.SetPropertyValueAction

class MockBlendableAnimator(
  val addAnimatorToRunningList: (MockBlendableAnimator) -> Unit
) : BlendableAnimator() {

  var started = false
    private set
  var isInfinite = false
    private set
  var interruptableEndActions: Map<SinglePropertyAnimation<*>, List<() -> Unit>> = mapOf()

  init {
    addAnimatorToRunningList(this)
  }

  override fun start() {
    started = true
    beforeStartActions
        .mapNotNull { it as? HoldHeightConstantAction }
        .forEach { it.idempotentAction() }
    innerAnimator.listeners?.forEach { it.onAnimationStart(innerAnimator) }
    interruptableEndActions
        .flatMap { entry -> entry.value }
        .filter { it !is SetPropertyValueAction }
        .forEach { action -> action() }
    innerAnimator.listeners?.forEach { it.onAnimationEnd(innerAnimator) }
  }

  override fun doOnFinishedUnlessLastAnimationInterrupted(endAction: () -> Unit) {
    val existingList = interruptableEndActions[animations.last()] ?: listOf()
    interruptableEndActions = interruptableEndActions + (animations.last() to (existingList + endAction))
  }

  override var repeatCount: Int
    get() = super.repeatCount
    set(count) {
      isInfinite = count == INFINITE
      super.repeatCount = count
    }

  override fun copyWithoutAnimations() = MockBlendableAnimator(addAnimatorToRunningList)
}
