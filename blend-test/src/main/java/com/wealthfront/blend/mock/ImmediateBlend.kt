package com.wealthfront.blend.mock

import android.animation.Animator
import android.annotation.SuppressLint
import com.wealthfront.blend.Blend
import com.wealthfront.blend.animator.BlendableAnimator
import com.wealthfront.blend.builder.AnimatorSetWrapper

/**
 * A testing replacement for [Blend] that runs all animations intstantly and without actually starting an [Animator].
 *
 * Tests requiring animation listeners should use this to avoid the robolectric issue described here:
 * https://github.com/robolectric/robolectric/issues/2930
 */
@SuppressLint("CheckResult")
class ImmediateBlend : Blend() {

  override fun createAnimatorSet(animator: Animator): AnimatorSetWrapper {
    return ImmediateAnimatorSetWrapper(animator)
  }

  override fun createBlendableAnimator(): BlendableAnimator = ImmediateBlendableAnimator()
}
