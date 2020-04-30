package com.wealthfront.blend.properties

import android.view.View
import com.google.common.truth.Truth.assertThat
import com.wealthfront.blend.animator.SinglePropertyAnimation
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations.initMocks

class AnimationDataTest {

  lateinit var animationData: AnimationData
  @Mock lateinit var view: View
  @Mock lateinit var property: AdditiveProperty<View>

  @Before
  fun setUp() {
    initMocks(this)
    animationData = AnimationData()
  }

  @Test
  fun getFutureValue_currentValue() {
    assertThat(animationData.futureValue).isNull()
  }

  @Test
  fun getFutureValue_futureValue() {
    val firstAnimation = SinglePropertyAnimation(view, property, 10f)
    val latestAnimation = SinglePropertyAnimation(view, property, 20f)
    animationData.addRunningAnimation(firstAnimation)
    animationData.addRunningAnimation(latestAnimation)
    assertThat(animationData.futureValue).isWithin(0.01f).of(20f)
  }

  @Test
  fun getFutureValue_futureValue_animationEndedEarly() {
    val firstAnimation = SinglePropertyAnimation(view, property, 10f)
    val latestAnimation = SinglePropertyAnimation(view, property, 20f)
    animationData.addRunningAnimation(firstAnimation)
    animationData.addRunningAnimation(latestAnimation)
    animationData.removeRunningAnimation(latestAnimation)
    assertThat(animationData.futureValue).isWithin(0.01f).of(20f)
  }

  @Test
  fun getFutureValue_futureValue_animationEndedEarly_clear() {
    val firstAnimation = SinglePropertyAnimation(view, property, 10f)
    val latestAnimation = SinglePropertyAnimation(view, property, 20f)
    animationData.addRunningAnimation(firstAnimation)
    animationData.addRunningAnimation(latestAnimation)
    animationData.removeRunningAnimation(latestAnimation)
    animationData.removeRunningAnimation(firstAnimation)
    assertThat(animationData.futureValue).isNull()
  }
}