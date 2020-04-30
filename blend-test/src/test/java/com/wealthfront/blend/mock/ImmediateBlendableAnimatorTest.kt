package com.wealthfront.blend.mock

import android.view.View
import com.google.common.truth.Truth.assertThat
import com.wealthfront.blend.ALPHA_FULL
import com.wealthfront.blend.ALPHA_TRANSPARENT
import com.wealthfront.blend.BuildConfig
import com.wealthfront.blend.animator.BlendableAnimator
import com.wealthfront.blend.animator.SinglePropertyAnimation
import com.wealthfront.blend.properties.AdditiveViewProperties.ALPHA
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations.initMocks
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment.application
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class ImmediateBlendableAnimatorTest {

  lateinit var animator: BlendableAnimator
  val view = View(application)

  @Before
  fun setUp() {
    initMocks(this)
    animator = ImmediateBlendableAnimator()
  }

  @Test
  fun runEndActions() {
    var callbackRan = false
    view.alpha = ALPHA_TRANSPARENT
    animator.addAnimation(
        SinglePropertyAnimation(
            subject = view,
            property = ALPHA,
            targetValue = ALPHA_FULL
        ))
    animator.doOnFinishedEvenIfInterrupted { callbackRan = true }
    animator.start()
    assertThat(view.alpha).isWithin(0.01f).of(ALPHA_FULL)
    assertThat(callbackRan).isTrue()
  }

  @Test
  fun runInterruptibleEndActions() {
    var callbackRan = false
    view.alpha = ALPHA_TRANSPARENT
    animator.addAnimation(
        SinglePropertyAnimation(
            subject = view,
            property = ALPHA,
            targetValue = ALPHA_FULL
        ))
    animator.doOnFinishedUnlessLastAnimationInterrupted { callbackRan = true }
    animator.start()
    assertThat(view.alpha).isWithin(0.01f).of(ALPHA_FULL)
    assertThat(callbackRan).isTrue()
  }
}