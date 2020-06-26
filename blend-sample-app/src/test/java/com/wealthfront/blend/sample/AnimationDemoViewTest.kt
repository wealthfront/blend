package com.wealthfront.blend.sample

import com.google.common.truth.Truth.assertThat
import com.wealthfront.blend.mock.ImmediateBlend
import com.wealthfront.blend.mock.MockBlend
import com.wealthfront.blend.properties.AdditiveViewProperties.Y
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

const val TOP_Y = 100f
const val BOTTOM_Y = 10f

@RunWith(RobolectricTestRunner::class)
class AnimationDemoViewTest {

  lateinit var view: AnimationDemoView
  lateinit var mockBlend: MockBlend
  lateinit var immediateBlend: ImmediateBlend

  @Before
  fun setUp() {
    view = AnimationDemoView(application)
    mockBlend = MockBlend()
    immediateBlend = ImmediateBlend()
    view.topY = TOP_Y
    view.bottomY = BOTTOM_Y
  }

  fun setUpWithMockBlend() {
    view.blend = mockBlend
  }

  fun setUpWithImmediateBlend() {
    view.blend = immediateBlend
  }

  @Test
  fun animateCircleWithBlend_mock() {
    setUpWithMockBlend()
    view.demoCircle.y = BOTTOM_Y
    view.animateCircleWithBlend(view.demoCircle)
    mockBlend.assertThat(view.demoCircle).hasPropertySetTo(Y, TOP_Y)
  }

  @Test
  fun animateCircleWithBlend_immediate() {
    setUpWithImmediateBlend()
    view.demoCircle.y = BOTTOM_Y
    view.animateCircleWithBlend(view.demoCircle)
    assertThat(view.demoCircle.y).isWithin(0.1f).of(TOP_Y)
    view.animateCircleWithBlend(view.demoCircle)
    assertThat(view.demoCircle.y).isWithin(0.1f).of(BOTTOM_Y)
  }
}