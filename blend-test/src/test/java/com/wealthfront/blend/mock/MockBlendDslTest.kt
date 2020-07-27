package com.wealthfront.blend.mock

import android.view.View
import android.view.ViewGroup
import com.google.common.truth.Truth.assertThat
import com.wealthfront.blend.builder.SetPropertyValueAction
import com.wealthfront.blend.dsl.collapse
import com.wealthfront.blend.dsl.crossfadeWith
import com.wealthfront.blend.dsl.expand
import com.wealthfront.blend.dsl.fadeIn
import com.wealthfront.blend.dsl.fadeOut
import com.wealthfront.blend.dsl.translationX
import com.wealthfront.blend.properties.AdditiveViewProperties.ALPHA
import com.wealthfront.blend.properties.AdditiveViewProperties.TRANSLATION_X
import com.wealthfront.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations.initMocks
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MockBlendDslTest {

  lateinit var blend: MockBlend
  @Mock lateinit var view1: View
  @Mock lateinit var layoutParams1: ViewGroup.LayoutParams
  @Mock lateinit var view2: View
  @Mock lateinit var layoutParams2: ViewGroup.LayoutParams

  @Before
  fun setUp() {
    initMocks(this)
    blend = MockBlend()
    whenever(view1.layoutParams).thenReturn(layoutParams1)
    whenever(view2.layoutParams).thenReturn(layoutParams2)
  }

  @Test(expected = AssertionError::class)
  fun clearMockData() {
    blend {
      target(view1).animations { collapse() }
    }.start()
    blend.clearMockData()
    blend.assertThat(view1).isCollapsed()
  }

  @Test
  fun assertThat_isNeverAnimated() {
    blend.assertThat(view1).isNeverAnimated()
  }

  @Test(expected = AssertionError::class)
  fun assertThat_isNeverAnimated_fails() {
    blend {
      target(view1).animations { collapse() }
    }.start()
    blend.assertThat(view1).isNeverAnimated()
  }

  @Test
  fun assertThat_hasPropertySetTo() {
    blend {
      target(view1).animations { translationX(100f) }
    }.start()
    blend.assertThat(view1).hasPropertySetTo(TRANSLATION_X, 100f)
  }

  @Test(expected = AssertionError::class)
  fun assertThat_hasPropertySetTo_fail() {
    blend {
      target(view1).animations { translationX(100f) }
    }.start()
    blend.assertThat(view1).hasPropertySetTo(TRANSLATION_X, 50f)
  }

  @Test
  fun assertThat_isCollapsed() {
    blend {
      target(view1).animations { collapse() }
    }.start()
    blend.assertThat(view1).isCollapsed()
  }

  @Test(expected = AssertionError::class)
  fun assertThat_isCollapsed_fail() {
    blend {
      target(view1).animations { expand() }
    }.start()
    blend.assertThat(view1).isCollapsed()
  }

  @Test
  fun assertThat_isExpanded() {
    blend {
      target(view1).animations { expand() }
    }.start()
    blend.assertThat(view1).isExpanded()
  }

  @Test(expected = AssertionError::class)
  fun assertThat_isExpanded_fail() {
    blend {
      target(view1).animations { collapse() }
    }.start()
    blend.assertThat(view1).isExpanded()
  }

  @Test
  fun assertThat_isFadedIn() {
    blend {
      target(view1).animations { fadeIn() }
    }.start()
    blend.assertThat(view1).isFadedIn()
  }

  @Test(expected = AssertionError::class)
  fun assertThat_isFadedIn_fail() {
    blend {
      target(view1).animations { fadeOut() }
    }.start()
    blend.assertThat(view1).isExpanded()
  }

  @Test
  fun assertThat_isFadedOut() {
    blend {
      target(view1).animations { crossfadeWith(view2) }
    }.start()
    blend.assertThat(view1).isFadedOut()
    blend.assertThat(view2).isFadedIn()
  }

  @Test(expected = AssertionError::class)
  fun assertThat_isFadedOut_fail() {
    blend {
      target(view1).animations { fadeIn() }
    }.start()
    blend.assertThat(view1).isExpanded()
  }

  @Test
  fun assertThat_multipleViews_sameAnimations() {
    blend {
      target(view1, view2).animations {
        collapse()
        expand()
        fadeIn()
      }
    }.start()
    blend.assertThat(view1).isExpanded()
    blend.assertThat(view2).isExpanded()
    blend.assertThat(view1).isFadedIn()
    blend.assertThat(view2).isFadedIn()
  }

  @Test
  fun assertThat_multipleViews_differentAnimations() {
    blend {
      target(view1).animations { collapse() }
      target(view2).animations { expand() }
    }.start()
    blend.assertThat(view1).isCollapsed()
    blend.assertThat(view2).isExpanded()
  }

  @Test(expected = AssertionError::class)
  fun assertThat_forgetToStartFails() {
    blend {
      target(view1).animations { collapse() }
    }
    blend.assertThat(view1).isCollapsed()
  }

  @Test
  fun runStartActions() {
    var callbackRan = false
    blend {
      doOnStart { callbackRan = true }
      target(view1).animations { fadeIn() }
    }.start()
    blend.assertThat(view1).isFadedIn()
    assertThat(callbackRan).isTrue()
  }

  @Test
  fun runEndActions() {
    var callbackRan = false
    blend {
      target(view1).animations { fadeIn() }
      doOnFinishedEvenIfInterrupted { callbackRan = true }
    }.start()
    blend.assertThat(view1).isFadedIn()
    assertThat(callbackRan).isTrue()
  }

  @Test
  fun runInterruptableEndActions() {
    var callbackRan = false
    blend {
      target(view1).animations { fadeIn() }
      doOnFinishedUnlessLastAnimationInterrupted { callbackRan = true }
    }.start()
    blend.assertThat(view1).isFadedIn()
    assertThat(callbackRan).isTrue()
  }

  @Test
  fun runInterruptableEndActions_exceptSetPropertyValueActions() {
    var callbackRan = false
    blend {
      target(view1).animations { fadeIn() }
      doOnFinishedUnlessLastAnimationInterrupted(object : SetPropertyValueAction(view1, ALPHA, 0.5f) {
        override fun invoke() {
          super.invoke()
          callbackRan = true
        }
      })
    }.start()
    blend.assertThat(view1).hasPropertySetTo(ALPHA, 0.5f)
    assertThat(callbackRan).isFalse()
  }
}