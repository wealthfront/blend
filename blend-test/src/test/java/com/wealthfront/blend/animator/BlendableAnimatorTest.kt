package com.wealthfront.blend.animator

import android.animation.Animator.AnimatorListener
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import com.google.common.truth.Truth.assertThat
import com.wealthfront.any
import com.wealthfront.blend.properties.AdditiveProperty
import com.wealthfront.blend.properties.AdditiveViewProperties.ALPHA
import com.wealthfront.blend.properties.AdditiveViewProperties.ELEVATION
import com.wealthfront.blend.properties.AdditiveViewProperties.HEIGHT
import com.wealthfront.blend.properties.AdditiveViewProperties.MARGIN_BOTTOM
import com.wealthfront.blend.properties.AdditiveViewProperties.MARGIN_LEFT
import com.wealthfront.blend.properties.AdditiveViewProperties.MARGIN_RIGHT
import com.wealthfront.blend.properties.AdditiveViewProperties.MARGIN_TOP
import com.wealthfront.blend.properties.AdditiveViewProperties.PADDING_BOTTOM
import com.wealthfront.blend.properties.AdditiveViewProperties.PADDING_LEFT
import com.wealthfront.blend.properties.AdditiveViewProperties.PADDING_RIGHT
import com.wealthfront.blend.properties.AdditiveViewProperties.PADDING_TOP
import com.wealthfront.blend.properties.AdditiveViewProperties.ROTATION
import com.wealthfront.blend.properties.AdditiveViewProperties.ROTATION_X
import com.wealthfront.blend.properties.AdditiveViewProperties.ROTATION_Y
import com.wealthfront.blend.properties.AdditiveViewProperties.TRANSLATION_X
import com.wealthfront.blend.properties.AdditiveViewProperties.TRANSLATION_Y
import com.wealthfront.blend.properties.AdditiveViewProperties.TRANSLATION_Z
import com.wealthfront.blend.properties.AdditiveViewProperties.WIDTH
import com.wealthfront.blend.properties.AdditiveViewProperties.X
import com.wealthfront.blend.properties.AdditiveViewProperties.Y
import com.wealthfront.blend.properties.AdditiveViewProperties.Z
import com.wealthfront.blend.properties.AnimationData
import com.wealthfront.eq
import com.wealthfront.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations.initMocks
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BlendableAnimatorTest {

  lateinit var blendableAnimator: BlendableAnimator
  lateinit var secondBlendableAnimator: BlendableAnimator
  @Mock lateinit var valueAnimator: ValueAnimator
  @Mock lateinit var secondValueAnimator: ValueAnimator
  @Mock lateinit var subject: View
  @Mock lateinit var subjectLayoutParams: ViewGroup.MarginLayoutParams

  @Captor lateinit var animatorUpdateListenerCaptor: ArgumentCaptor<AnimatorUpdateListener?>
  @Captor lateinit var animatorListenerCaptor: ArgumentCaptor<AnimatorListener?>

  var animationData: AnimationData? = null

  @Before
  fun setUp() {
    initMocks(this)
    blendableAnimator = BlendableAnimator()
    blendableAnimator.innerAnimator = valueAnimator
    secondBlendableAnimator = BlendableAnimator()
    secondBlendableAnimator.innerAnimator = secondValueAnimator
    subject.layoutParams = subjectLayoutParams
  }

  @Test
  fun animateValue_singleAnimator() {
    blendableAnimator.addAnimation(SinglePropertyAnimation(
        subject = subject,
        property = X,
        targetValue = 100f
    ))
    blendableAnimator.interpolator = LinearInterpolator()
    whenever(X.getCurrentValue(subject)).thenReturn(0f)

    blendableAnimator.start()

    verify(valueAnimator).addUpdateListener(animatorUpdateListenerCaptor.capture())
    val updateListener = animatorUpdateListenerCaptor.value!!

    whenever(X.getCurrentValue(subject)).thenReturn(0f)
    sendTime(updateListener, 0f)
    verify(subject).x = 0f

    whenever(X.getCurrentValue(subject)).thenReturn(0f)
    sendTime(updateListener, 0.5f)
    verify(subject).x = 50f

    whenever(X.getCurrentValue(subject)).thenReturn(50f)
    sendTime(updateListener, 1f)
    verify(subject).x = 100f
  }

  @Test
  fun animateValue_multipleAnimators() {
    blendableAnimator.addAnimation(SinglePropertyAnimation(
        subject = subject,
        property = X,
        targetValue = 100f
    ))
    blendableAnimator.interpolator = LinearInterpolator()
    whenever(X.getCurrentValue(subject)).thenReturn(0f)

    blendableAnimator.start()

    verify(valueAnimator).addUpdateListener(animatorUpdateListenerCaptor.capture())
    val updateListener = animatorUpdateListenerCaptor.value!!

    whenever(X.getCurrentValue(subject)).thenReturn(0f)
    sendTime(updateListener, 0f)
    verify(subject).x = 0f

    whenever(X.getCurrentValue(subject)).thenReturn(50f)
    sendTime(updateListener, 0.5f)
    verify(subject).x = 100f

    whenever(X.getCurrentValue(subject)).thenReturn(0f)
    sendTime(updateListener, 1f)
    verify(subject).x = 50f
  }

  @Test
  fun animateValue_multipleAnimators_respectStartedAnimations() {
    setUpTag(subject, X)
    blendableAnimator.addAnimation(SinglePropertyAnimation(
        subject = subject,
        property = X,
        targetValue = 100f
    ))
    blendableAnimator.interpolator = LinearInterpolator()
    secondBlendableAnimator.addAnimation(SinglePropertyAnimation(
        subject = subject,
        property = X,
        targetValue = 200f
    ))
    secondBlendableAnimator.interpolator = LinearInterpolator()
    whenever(X.getCurrentValue(subject)).thenReturn(0f)
    whenever(valueAnimator.isStarted).thenReturn(true)

    blendableAnimator.start()
    secondBlendableAnimator.start()

    verify(secondValueAnimator).addUpdateListener(animatorUpdateListenerCaptor.capture())
    val updateListener = animatorUpdateListenerCaptor.value!!

    whenever(X.getCurrentValue(subject)).thenReturn(0f)
    sendTime(updateListener, 0f)
    verify(subject).x = 0f

    whenever(X.getCurrentValue(subject)).thenReturn(0f)
    sendTime(updateListener, 0.5f)
    verify(subject).x = 50f

    whenever(X.getCurrentValue(subject)).thenReturn(50f)
    sendTime(updateListener, 1f)
    verify(subject).x = 100f
  }

  @Test
  fun animateValue_withEveryFrameAction() {
    var count = 0
    blendableAnimator.addAnimation(SinglePropertyAnimation(
        subject = subject,
        property = X,
        targetValue = 100f
    ))
    blendableAnimator.doEveryFrame { count++ }
    blendableAnimator.interpolator = LinearInterpolator()
    whenever(X.getCurrentValue(subject)).thenReturn(0f)

    blendableAnimator.start()

    verify(valueAnimator).addUpdateListener(animatorUpdateListenerCaptor.capture())
    val updateListener = animatorUpdateListenerCaptor.value!!

    whenever(X.getCurrentValue(subject)).thenReturn(0f)
    sendTime(updateListener, 0f)
    assertThat(count).isEqualTo(1)

    whenever(X.getCurrentValue(subject)).thenReturn(0f)
    sendTime(updateListener, 0.5f)
    assertThat(count).isEqualTo(2)

    whenever(X.getCurrentValue(subject)).thenReturn(50f)
    sendTime(updateListener, 1f)
    assertThat(count).isEqualTo(3)
  }

  @Test
  fun animateValue_intAnimators_smallValue() {
    listOf(
        WIDTH,
        HEIGHT,
        PADDING_LEFT,
        PADDING_RIGHT,
        PADDING_TOP,
        PADDING_BOTTOM,
        MARGIN_LEFT,
        MARGIN_RIGHT,
        MARGIN_TOP,
        MARGIN_BOTTOM
    ).forEach { property ->
      assertThat(property.interpolate(0f, 1f, 0f, subject))
          .isWithin(0.01f).of(0f)
      assertThat(property.interpolate(0f, 1f, 0.3f, subject))
          .isWithin(0.01f).of(0f)
      assertThat(property.interpolate(0f, 1f, 0.7f, subject))
          .isWithin(0.01f).of(1f)
      assertThat(property.interpolate(0f, 1f, 1f, subject))
          .isWithin(0.01f).of(1f)
    }
  }

  @Test
  fun animateValue_floatAnimators_smallValue() {
    listOf(
        ALPHA,
        X,
        Y,
        Z,
        TRANSLATION_X,
        TRANSLATION_Y,
        TRANSLATION_Z,
        ROTATION,
        ROTATION_X,
        ROTATION_Y,
        ELEVATION
    ).forEach { property ->
      assertThat(property.interpolate(0f, 1f, 0f, subject))
          .isWithin(0.01f).of(0f)
      assertThat(property.interpolate(0f, 1f, 0.3f, subject))
          .isWithin(0.01f).of(0.3f)
      assertThat(property.interpolate(0f, 1f, 0.7f, subject))
          .isWithin(0.01f).of(0.7f)
      assertThat(property.interpolate(0f, 1f, 1f, subject))
          .isWithin(0.01f).of(1f)
    }
  }

  @Test
  fun doWhenFinishedUnlessInterrupted_notInterrupted() {
    var finished = false
    setUpTag(subject, X)
    blendableAnimator.addAnimation(SinglePropertyAnimation(
        subject = subject,
        property = X,
        targetValue = 100f
    ))
    blendableAnimator.interpolator = LinearInterpolator()
    blendableAnimator.doOnFinishedUnlessLastAnimationInterrupted { finished = true }
    whenever(X.getCurrentValue(subject)).thenReturn(0f)

    blendableAnimator.start()

    verify(valueAnimator).addListener(animatorListenerCaptor.capture())
    val listener = animatorListenerCaptor.value!!

    listener.onAnimationEnd(valueAnimator)
    assertThat(finished).isTrue()
  }

  @Test
  fun doWhenFinishedUnlessInterrupted_interrupted() {
    var finished = false
    setUpTag(subject, X)
    blendableAnimator.addAnimation(SinglePropertyAnimation(
        subject = subject,
        property = X,
        targetValue = 100f
    ))
    blendableAnimator.interpolator = LinearInterpolator()
    blendableAnimator.doOnFinishedUnlessLastAnimationInterrupted { finished = true }
    secondBlendableAnimator.addAnimation(SinglePropertyAnimation(
        subject = subject,
        property = X,
        targetValue = 200f
    ))
    whenever(X.getCurrentValue(subject)).thenReturn(0f)

    blendableAnimator.start()
    secondBlendableAnimator.start()

    verify(valueAnimator).addListener(animatorListenerCaptor.capture())
    val listener = animatorListenerCaptor.value!!

    listener.onAnimationEnd(valueAnimator)
    assertThat(finished).isFalse()
  }

  @Test
  fun doWhenFinishedEvenIfInterrupted() {
    var finished = false
    blendableAnimator.addAnimation(SinglePropertyAnimation(
        subject = subject,
        property = X,
        targetValue = 100f
    ))
    blendableAnimator.doOnFinishedEvenIfInterrupted { finished = true }

    blendableAnimator.start()

    verify(valueAnimator, times(2)).addListener(animatorListenerCaptor.capture())
    val listeners = animatorListenerCaptor.allValues!!

    listeners.forEach { it!!.onAnimationEnd(valueAnimator) }
    assertThat(finished).isTrue()
  }

  @Test
  fun doOnStart() {
    var started = false
    blendableAnimator.addAnimation(SinglePropertyAnimation(
        subject = subject,
        property = X,
        targetValue = 100f
    ))
    blendableAnimator.doOnStart { started = true }

    blendableAnimator.start()

    verify(valueAnimator, times(2)).addListener(animatorListenerCaptor.capture())
    val listeners = animatorListenerCaptor.allValues!!

    listeners.forEach { it!!.onAnimationStart(valueAnimator) }
    assertThat(started).isTrue()
  }

  fun sendTime(
    animatorUpdateListener: AnimatorUpdateListener,
    time: Float
  ) {
    whenever(valueAnimator.animatedFraction).thenReturn(time)
    animatorUpdateListener.onAnimationUpdate(valueAnimator)
  }

  fun setUpTag(
    target: View,
    property: AdditiveProperty<View>
  ) {
    whenever(target.setTag(eq(property.id), any())).then {
      animationData = it.getArgument(1) as AnimationData
      Unit
    }
    whenever(target.getTag(property.id)).then { animationData }
  }
}