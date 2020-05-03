package com.wealthfront.blend.properties

import android.view.View
import com.google.common.truth.Truth.assertThat
import com.wealthfront.blend.animator.BlendableAnimator
import com.wealthfront.blend.animator.SinglePropertyAnimation
import com.wealthfront.whenever
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations.initMocks

class AnimationDataTest {

  lateinit var animationData: AnimationData
  @Mock lateinit var view: View
  @Mock lateinit var property: AdditiveProperty<View>
  @Mock lateinit var startedAnimator: BlendableAnimator
  @Mock lateinit var unstartedAnimator: BlendableAnimator

  @Before
  fun setUp() {
    initMocks(this)
    animationData = AnimationData()

    whenever(startedAnimator.isStarted).thenReturn(true)
  }

  @Test
  fun getFutureValue_currentValue() {
    assertThat(animationData.futureValue).isNull()
  }

  @Test
  fun getFutureValue_futureValue() {
    val firstAnimation = SinglePropertyAnimation(view, property, 10f)
      .apply {
        animator = startedAnimator
        isPartOfAFullyQueuedSet = true
      }
    val latestAnimation = SinglePropertyAnimation(view, property, 20f)
      .apply {
        animator = startedAnimator
        isPartOfAFullyQueuedSet = true
      }
    animationData.addQueuedAnimation(firstAnimation)
    animationData.addQueuedAnimation(latestAnimation)
    assertThat(animationData.futureValue).isWithin(0.01f).of(20f)
  }

  @Test
  fun getFutureValue_futureValue_animationEndedEarly() {
    val firstAnimation = SinglePropertyAnimation(view, property, 10f)
      .apply {
        animator = startedAnimator
        isPartOfAFullyQueuedSet = true
      }
    val latestAnimation = SinglePropertyAnimation(view, property, 20f)
      .apply {
        animator = startedAnimator
        isPartOfAFullyQueuedSet = true
      }
    animationData.addQueuedAnimation(firstAnimation)
    animationData.addQueuedAnimation(latestAnimation)
    animationData.removeQueuedAnimation(latestAnimation)
    assertThat(animationData.futureValue).isWithin(0.01f).of(20f)
  }

  @Test
  fun getFutureValue_futureValue_animationEndedEarly_clear() {
    val firstAnimation = SinglePropertyAnimation(view, property, 10f)
      .apply {
        animator = startedAnimator
        isPartOfAFullyQueuedSet = true
      }
    val latestAnimation = SinglePropertyAnimation(view, property, 20f)
      .apply {
        animator = startedAnimator
        isPartOfAFullyQueuedSet = true
      }
    animationData.addQueuedAnimation(firstAnimation)
    animationData.addQueuedAnimation(latestAnimation)
    animationData.removeQueuedAnimation(latestAnimation)
    animationData.removeQueuedAnimation(firstAnimation)
    assertThat(animationData.futureValue).isNull()
  }

  @Test
  fun removeChainedAnimation_newAnimationAdded() {
    val delayedAnimation = SinglePropertyAnimation(view, property, 10f)
      .apply {
        animator = unstartedAnimator
        isPartOfAFullyQueuedSet = true
      }
    val newAnimation = SinglePropertyAnimation(view, property, 20f)
    animationData.addQueuedAnimation(delayedAnimation)
    animationData.addQueuedAnimation(newAnimation)
    animationData.removeQueuedAnimation(newAnimation)
    assertThat(animationData.futureValue).isNull()
  }

  @Test
  fun doNotRemoveChainedAnimation_newAnimationAdded_chainedAnimationIsPartOfUnfinishedSet() {
    val delayedAnimation = SinglePropertyAnimation(view, property, 10f)
      .apply {
        animator = unstartedAnimator
        isPartOfAFullyQueuedSet = false
      }
    val newAnimation = SinglePropertyAnimation(view, property, 20f)
    animationData.addQueuedAnimation(delayedAnimation)
    animationData.addQueuedAnimation(newAnimation)
    animationData.removeQueuedAnimation(newAnimation)
    delayedAnimation.animator = startedAnimator
    assertThat(animationData.futureValue).isNotNull()
  }
}