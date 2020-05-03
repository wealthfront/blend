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
        isPartOfAFullyCommittedSet = true
      }
    val latestAnimation = SinglePropertyAnimation(view, property, 20f)
      .apply {
        animator = startedAnimator
        isPartOfAFullyCommittedSet = true
      }
    animationData.addCommittedAnimation(firstAnimation)
    animationData.addCommittedAnimation(latestAnimation)
    assertThat(animationData.futureValue).isWithin(0.01f).of(20f)
  }

  @Test
  fun getFutureValue_futureValue_animationEndedEarly() {
    val firstAnimation = SinglePropertyAnimation(view, property, 10f)
      .apply {
        animator = startedAnimator
        isPartOfAFullyCommittedSet = true
      }
    val latestAnimation = SinglePropertyAnimation(view, property, 20f)
      .apply {
        animator = startedAnimator
        isPartOfAFullyCommittedSet = true
      }
    animationData.addCommittedAnimation(firstAnimation)
    animationData.addCommittedAnimation(latestAnimation)
    animationData.removeCommittedAnimation(latestAnimation)
    assertThat(animationData.futureValue).isWithin(0.01f).of(20f)
  }

  @Test
  fun getFutureValue_futureValue_animationEndedEarly_clear() {
    val firstAnimation = SinglePropertyAnimation(view, property, 10f)
      .apply {
        animator = startedAnimator
        isPartOfAFullyCommittedSet = true
      }
    val latestAnimation = SinglePropertyAnimation(view, property, 20f)
      .apply {
        animator = startedAnimator
        isPartOfAFullyCommittedSet = true
      }
    animationData.addCommittedAnimation(firstAnimation)
    animationData.addCommittedAnimation(latestAnimation)
    animationData.removeCommittedAnimation(latestAnimation)
    animationData.removeCommittedAnimation(firstAnimation)
    assertThat(animationData.futureValue).isNull()
  }

  @Test
  fun removeChainedAnimation_newAnimationAdded() {
    val delayedAnimation = SinglePropertyAnimation(view, property, 10f)
      .apply {
        animator = unstartedAnimator
        isPartOfAFullyCommittedSet = true
      }
    val newAnimation = SinglePropertyAnimation(view, property, 20f)
    animationData.addCommittedAnimation(delayedAnimation)
    animationData.addCommittedAnimation(newAnimation)
    animationData.removeCommittedAnimation(newAnimation)
    assertThat(animationData.futureValue).isNull()
  }

  @Test
  fun doNotRemoveChainedAnimation_newAnimationAdded_chainedAnimationIsPartOfUnfinishedSet() {
    val delayedAnimation = SinglePropertyAnimation(view, property, 10f)
        .apply {
          animator = unstartedAnimator
          isPartOfAFullyCommittedSet = false
        }
    val newAnimation = SinglePropertyAnimation(view, property, 20f)
    animationData.addCommittedAnimation(delayedAnimation)
    animationData.addCommittedAnimation(newAnimation)
    animationData.removeCommittedAnimation(newAnimation)
    delayedAnimation.animator = startedAnimator
    assertThat(animationData.futureValue).isNotNull()
  }
}