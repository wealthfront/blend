package com.wealthfront.blend

import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat.getColor
import com.google.common.truth.Truth.assertThat
import com.wealthfront.ViewAssertions.assertThatView
import com.wealthfront.blend.animator.BlendableAnimator
import com.wealthfront.blend.mock.ImmediateBlend
import com.wealthfront.blend.properties.AdditiveProperty
import com.wealthfront.blend.properties.AnimationData
import com.wealthfront.ktx.matchParentHeight
import com.wealthfront.ktx.wrapContentHeight
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment.application
import org.robolectric.annotation.Config

// Tests requiring animation listeners use an `Instant` subclass of Blend to avoid the robolectric issue
// described here: https://github.com/robolectric/robolectric/issues/2930
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class BlendDslTest {

  val blend: Blend = Blend()
  val immediateBlend: ImmediateBlend = ImmediateBlend()
  val view = View(application)
  val textView = TextView(application)
  val viewGroup = LinearLayout(application)

  val DEFAULT_HEIGHT = 300
  val DEFAULT_WIDTH = 301

  val SCALED_HEIGHT = 200
  val SCALED_WIDTH = 201

  @Before
  fun setUp() {
    view.layoutParams = ViewGroup.LayoutParams(DEFAULT_WIDTH, DEFAULT_HEIGHT)
    view.measure(
        View.MeasureSpec.makeMeasureSpec(DEFAULT_WIDTH, EXACTLY),
        View.MeasureSpec.makeMeasureSpec(DEFAULT_HEIGHT, EXACTLY))
    view.layout(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT)

    textView.layoutParams = LinearLayout.LayoutParams(DEFAULT_WIDTH, DEFAULT_HEIGHT)

    viewGroup.apply {
      removeAllViews()
      layoutParams = ViewGroup.LayoutParams(DEFAULT_WIDTH, DEFAULT_HEIGHT)
      addView(view)
      measure(
          View.MeasureSpec.makeMeasureSpec(DEFAULT_WIDTH, EXACTLY),
          View.MeasureSpec.makeMeasureSpec(DEFAULT_HEIGHT, EXACTLY))
      layout(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT)
    }
  }

  @Test
  fun staggerByTarget_properDelays() {
    val testObjects = listOf(
        TestObject(0f, 0),
        TestObject(0f, 1),
        TestObject(0f, 2),
        TestObject(0f, 3)
    )
    val builder = blend {
      immediate()
      stagger(testObjects, 10) { subject ->
        target(subject).animations { genericProperty(10f, TestObjectProperty()) }
      }
    }

    assertThat(builder.animatorSet.childAnimators.size).isEqualTo(4)
    assertThat((builder.animatorSet.childAnimators[0] as BlendableAnimator).allAnimations
        .all { it.subject == testObjects[0] }).isTrue()
    assertThat(builder.animatorSet.childAnimators[0].startDelay).isEqualTo(0)
    assertThat((builder.animatorSet.childAnimators[1] as BlendableAnimator).allAnimations
        .all { it.subject == testObjects[1] }).isTrue()
    assertThat(builder.animatorSet.childAnimators[1].startDelay).isEqualTo(10)
    assertThat((builder.animatorSet.childAnimators[2] as BlendableAnimator).allAnimations
        .all { it.subject == testObjects[2] }).isTrue()
    assertThat(builder.animatorSet.childAnimators[2].startDelay).isEqualTo(20)
    assertThat((builder.animatorSet.childAnimators[3] as BlendableAnimator).allAnimations
        .all { it.subject == testObjects[3] }).isTrue()
    assertThat(builder.animatorSet.childAnimators[3].startDelay).isEqualTo(30)
  }

  @Test
  fun staggerByTarget_properValues() {
    val testObjects = listOf(
        TestObject(0f, 0),
        TestObject(0f, 1),
        TestObject(0f, 2),
        TestObject(0f, 3)
    )
    val builder = immediateBlend {
      stagger(testObjects, 0) { subject ->
        target(subject).animations { genericProperty(10f, TestObjectProperty()) }
      }
    }

    assertThat(builder.animatorSet.childAnimators.size).isEqualTo(4)
    builder.start()

    assertThat(testObjects[0].someProperty).isWithin(0.01f).of(10f)
    assertThat(testObjects[1].someProperty).isWithin(0.01f).of(10f)
    assertThat(testObjects[2].someProperty).isWithin(0.01f).of(10f)
    assertThat(testObjects[3].someProperty).isWithin(0.01f).of(10f)
  }

  @Test
  fun fadeIn_fromInvisible() {
    view.visibility = INVISIBLE
    immediateBlend {
      target(view).animations { fadeIn() }
    }.start()
    assertThatView(view).isFadedIn()
  }

  @Test
  fun fadeIn_fromVisible() {
    view.alpha = 0f
    immediateBlend {
      target(view).animations { fadeIn() }
    }.start()
    assertThatView(view).isFadedIn()
  }

  @Test
  fun fadeIn_fromGone() {
    view.visibility = GONE
    immediateBlend {
      target(view).animations { fadeIn() }
    }.start()
    assertThat(view.visibility).isEqualTo(GONE)
    assertThat(view.alpha).isWithin(.01f).of(ALPHA_FULL)
  }

  @Test
  fun fadeOut() {
    view.alpha = 1f
    view.visibility = VISIBLE
    immediateBlend {
      target(view).animations { fadeOut() }
    }.start()
    assertThatView(view).isFadedOut()
  }

  @Test
  fun crossfadeWith() {
    view.alpha = 1f
    textView.alpha = 0f
    textView.visibility = INVISIBLE
    immediateBlend {
      target(view).animations { crossfadeWith(textView) }
    }.start()
    assertThatView(view).isFadedOut()
    assertThatView(textView).isFadedIn()
  }

  @Test
  fun fadeOutThenFadeIn() {
    view.alpha = 1f
    textView.alpha = 0f
    immediateBlend.fadeOutThenFadeIn(view, textView)
    assertThatView(view).isFadedOut()
    assertThatView(textView).isFadedIn()
  }

  @Test
  fun expand() {
    view.layoutParams.height = 0
    immediateBlend {
      target(view).animations { expand() }
    }.start()
    assertThatView(view).isExpanded()
  }

  @Test
  fun expand_fromInvisible() {
    view.layoutParams.height = 0
    view.visibility = INVISIBLE
    immediateBlend {
      target(view).animations { expand() }
    }.start()
    assertThatView(view).isExpanded()
    assertThat(view.visibility).isEqualTo(INVISIBLE)
  }

  @Test
  fun expand_fromGone() {
    viewGroup.visibility = GONE
    immediateBlend {
      target(viewGroup).animations { expand() }
    }.start()
    assertThatView(viewGroup).isExpanded()
  }

  @Test
  fun expand_toWrapContentHeight() {
    viewGroup.layoutParams.height = 0
    blend {
      immediate()
      target(viewGroup).animations { height(viewGroup.wrapContentHeight) }
    }.start()
    assertThat(viewGroup.height).isEqualTo(viewGroup.wrapContentHeight)
  }

  @Test
  fun height_matchParent() {
    view.layoutParams.height = 1
    immediateBlend {
      target(view).animations { height(MATCH_PARENT) }
    }.start()
    assertThat(view.layoutParams.height).isEqualTo(MATCH_PARENT)
  }

  @Test
  fun height_matchParentHeight() {
    view.layoutParams.height = 1
    blend {
      immediate()
      target(view).animations { height(view.matchParentHeight) }
    }.start()
    assertThat(view.layoutParams.height).isEqualTo(view.matchParentHeight)
  }

  @Test
  fun collapse() {
    viewGroup.layoutParams.height = DEFAULT_HEIGHT
    immediateBlend {
      target(viewGroup).animations { collapse() }
    }.start()
    assertThatView(viewGroup).isCollapsed()
  }

  @Test
  fun expandAfter() {
    viewGroup.layoutParams.height = WRAP_CONTENT
    view.layoutParams.height = 10
    immediateBlend {
      target(viewGroup).animations {
        expandAfter { view.layoutParams.height = 20 }
      }
    }.start()
    assertThatView(viewGroup).isExpanded()
  }

  @Test
  fun expandAfter_matchParent() {
    viewGroup.layoutParams.height = MATCH_PARENT
    view.layoutParams.height = 10
    immediateBlend {
      target(viewGroup).animations {
        expandAfter { view.layoutParams.height = 20 }
      }
    }.start()
    assertThatView(viewGroup).isExpanded()
  }

  @Test
  fun expandAfter_specificHeight() {
    viewGroup.layoutParams.height = 30
    view.layoutParams.height = 10
    immediateBlend {
      target(viewGroup).animations {
        expandAfter { view.layoutParams.height = 20 }
      }
    }.start()
    assertThatView(viewGroup).isExpanded()
  }

  @Test
  fun expandAfter_gone() {
    viewGroup.layoutParams.height = 30
    viewGroup.visibility = GONE
    view.layoutParams.height = 10
    immediateBlend {
      target(viewGroup).animations {
        expandAfter { view.layoutParams.height = 20 }
      }
    }.start()
    assertThatView(viewGroup).isExpanded()
  }

  @Test
  fun textColorTest() {
    textView.setTextColor(getColor(application, R.color.primary_text_default_material_light))
    blend {
      immediate()
      target(textView).animations { textColor(R.color.secondary_text_default_material_light) }
    }.start()
    assertThat(textView.currentTextColor).isEqualTo(getColor(application, R.color.secondary_text_default_material_light))
  }

  @Test
  fun floatProperty() {
    val testObject = TestObject(0f, 0)
    blend {
      immediate()
      target(testObject).animations { genericProperty(10f, TestObjectProperty()) }
    }.start()
    assertThat(testObject.someProperty).isWithin(0.01f).of(10f)
  }

  private class TestObjectProperty : AdditiveProperty<TestObject> {
    companion object StaticMap {
      val animationDatas: MutableMap<TestObject, AnimationData> = mutableMapOf()
    }

    override val id: Int = 1

    override fun setValue(subject: TestObject, value: Float) {
      subject.someProperty = value
    }

    override fun getCurrentValue(subject: TestObject): Float = subject.someProperty

    override fun getAnimationData(subject: TestObject): AnimationData {
      return animationDatas[subject] ?: AnimationData().also { animationDatas[subject] = it }
    }

    override fun setUpOnAnimationCommitted(subject: TestObject) {
      addInterruptableEndActions(subject, {
        animationDatas.remove(subject)
      })
    }
  }

  private data class TestObject(var someProperty: Float, @ColorInt var color: Int)
}