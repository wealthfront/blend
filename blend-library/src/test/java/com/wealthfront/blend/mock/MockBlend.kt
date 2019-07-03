package com.wealthfront.blend.mock

import android.animation.Animator
import android.view.View
import com.wealthfront.blend.Blend
import com.wealthfront.blend.animator.SinglePropertyAnimation

/**
 * A mock version of [Blend] that allows for verification that animations were started.
 *
 * To verify animations, call [MockBlend.assertThat]. See [ViewBlendValidator]
 */
class MockBlend : Blend() {

  val animators: MutableList<MockBlendableAnimator> = mutableListOf()
  val viewsThatHaveStoppedPulsing: MutableList<View> = mutableListOf()

  override fun createAnimatorSet(animator: Animator) =
      ImmediateAnimatorSetWrapper(animator)

  override fun createBlendableAnimator() = MockBlendableAnimator { animators += it }

  /**
   * The entry point for animation assertions.
   */
  fun assertThat(subject: View): ViewBlendValidator {
    val activeAnimators = animators.filter { animator -> animator.started }
    val animations = activeAnimators.flatMap { animator ->
      animator.animations
          .filter { animation -> animation.subject is View }
          .mapNotNull { animation ->
            @Suppress("UNCHECKED_CAST")
            animation as? SinglePropertyAnimation<View>
          }
    }
    val interruptableEndActions: Map<SinglePropertyAnimation<*>, List<() -> Unit>> = activeAnimators
        .fold(mapOf()) { acc, mockBlendableAnimator ->
          acc + mockBlendableAnimator.interruptableEndActions
        }
    return ViewBlendValidator(
        subject,
        animations,
        interruptableEndActions,
        animators.any { it.isInfinite },
        viewsThatHaveStoppedPulsing.toList()
    )
  }

  override fun stopPulsing(vararg views: View) {
    viewsThatHaveStoppedPulsing += views
    super.stopPulsing(*views)
  }

  fun clearMockData() {
    animators.clear()
    viewsThatHaveStoppedPulsing.clear()
  }
}
