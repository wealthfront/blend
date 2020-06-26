package com.wealthfront

import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.MeasureSpec.UNSPECIFIED
import android.view.View.MeasureSpec.makeMeasureSpec
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import com.google.common.truth.ExpectFailure.SimpleSubjectBuilderCallback
import com.google.common.truth.ExpectFailure.expectFailureAbout
import com.google.common.truth.SimpleSubjectBuilder
import com.wealthfront.ThemedApplicationProvider.application
import com.wealthfront.ViewAssertions.assertThatView
import com.wealthfront.ViewSubject.Companion.VIEW_SUBJECT_FACTORY
import com.wealthfront.ktx.wrapContentHeight
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ViewSubjectTest {

  val view = View(application)
  val viewGroup = FrameLayout(application)
  val parent = FrameLayout(application)

  val DEFAULT_HEIGHT = 300
  val DEFAULT_WIDTH = 301
  val PARENT_HEIGHT = 400
  val PARENT_WIDTH = 401

  @Before
  fun setUp() {
    view.layoutParams = ViewGroup.LayoutParams(DEFAULT_WIDTH, DEFAULT_HEIGHT)
    viewGroup.layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
    parent.layoutParams = ViewGroup.LayoutParams(PARENT_WIDTH, PARENT_HEIGHT)
    viewGroup.addView(view)
    parent.addView(viewGroup)
    parent.measure(makeMeasureSpec(0, UNSPECIFIED), makeMeasureSpec(0, UNSPECIFIED))
    parent.layout(0, 0, PARENT_WIDTH, PARENT_HEIGHT)
  }

  @Test
  fun isExpanded() {
    viewGroup.layoutParams.height = WRAP_CONTENT
    viewGroup.visibility = VISIBLE
    assertThatView(viewGroup).isExpanded()
    expectFailure { whenTesting -> whenTesting.that(viewGroup).isCollapsed() }
  }

  @Test
  fun isCollapsed() {
    viewGroup.layoutParams.height = 0
    viewGroup.visibility = VISIBLE
    assertThatView(viewGroup).isCollapsed()
    expectFailure { whenTesting -> whenTesting.that(viewGroup).isExpanded() }
  }

  @Test
  fun isExpanded_invisible() {
    viewGroup.layoutParams.height = WRAP_CONTENT
    viewGroup.visibility = INVISIBLE
    assertThatView(viewGroup).isExpanded()
  }

  @Test
  fun isCollapsed_invisible() {
    viewGroup.layoutParams.height = 0
    viewGroup.visibility = INVISIBLE
    assertThatView(viewGroup).isCollapsed()
  }

  @Test
  fun isCollapsed_gone() {
    viewGroup.layoutParams.height = 0
    viewGroup.visibility = GONE
    assertThatView(viewGroup).isCollapsed()
  }

  @Test
  fun isCollapsed_goneWithHeight() {
    viewGroup.layoutParams.height = 300
    viewGroup.visibility = GONE
    assertThatView(viewGroup).isCollapsed()
  }

  @Test
  fun isNotExpandedOrCollapsed() {
    viewGroup.layoutParams.height = DEFAULT_HEIGHT / 2
    viewGroup.visibility = VISIBLE
    expectFailure { whenTesting -> whenTesting.that(viewGroup).isExpanded() }
    expectFailure { whenTesting -> whenTesting.that(viewGroup).isCollapsed() }
  }

  @Test
  fun isNotExpandedOrCollapsed_wrapContentHeight() {
    viewGroup.layoutParams.height = viewGroup.wrapContentHeight
    viewGroup.visibility = VISIBLE
    expectFailure { whenTesting -> whenTesting.that(viewGroup).isExpanded() }
    expectFailure { whenTesting -> whenTesting.that(viewGroup).isCollapsed() }
  }

  @Test
  fun isNotExpandedOrCollapsed_matchParent() {
    viewGroup.layoutParams.height = MATCH_PARENT
    viewGroup.visibility = VISIBLE
    expectFailure { whenTesting -> whenTesting.that(viewGroup).isExpanded() }
    expectFailure { whenTesting -> whenTesting.that(viewGroup).isCollapsed() }
  }

  @Test
  fun isNotExpanded_gone() {
    viewGroup.layoutParams.height = WRAP_CONTENT
    viewGroup.visibility = GONE
    expectFailure { whenTesting -> whenTesting.that(viewGroup).isExpanded() }
  }

  @Test
  fun isFadedOut() {
    view.alpha = ALPHA_TRANSPARENT
    view.visibility = INVISIBLE
    assertThatView(view).isFadedOut()
    expectFailure { whenTesting -> whenTesting.that(view).isFadedIn() }
  }

  @Test
  fun isFadedOut_nonZeroAlpha() {
    view.alpha = 0.5f
    view.visibility = INVISIBLE
    expectFailure { whenTesting -> whenTesting.that(view).isFadedIn() }
    assertThatView(view).isFadedOut()
  }

  @Test
  fun isFadedIn() {
    view.alpha = ALPHA_FULL
    view.visibility = VISIBLE
    assertThatView(view).isFadedIn()
    expectFailure { whenTesting -> whenTesting.that(view).isFadedOut() }
  }

  @Test
  fun isNotFadedIn() {
    view.alpha = 0.5f
    view.visibility = VISIBLE
    expectFailure { whenTesting -> whenTesting.that(view).isFadedIn() }
    expectFailure { whenTesting -> whenTesting.that(view).isFadedOut() }
  }

  private fun expectFailure(callback: (SimpleSubjectBuilder<ViewSubject, View?>) -> Unit): AssertionError {
    return expectFailureAbout(VIEW_SUBJECT_FACTORY, SimpleSubjectBuilderCallback { callback(it) })
  }
}
