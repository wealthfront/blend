package com.wealthfront

import android.view.View
import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.google.common.truth.Subject.Factory
import com.google.common.truth.Truth.assertAbout
import com.wealthfront.ViewSubject.Companion.VIEW_SUBJECT_FACTORY

class ViewSubject(failureMetadata: FailureMetadata, view: View?) : Subject<ViewSubject, View?>(failureMetadata, view) {

  companion object {
    val VIEW_SUBJECT_FACTORY: Factory<ViewSubject, View?> = Factory { failureMetadata, view ->
      ViewSubject(failureMetadata, view)
    }
  }

  fun isExpanded() = checkNotNullThen { actual ->
    if (!actual.isExpanded) {
      fail("is expanded: height is ${actual.layoutParams.height}, visibility is ${actual.visibility}")
    }
  }

  fun isCollapsed() = checkNotNullThen { actual ->
    if (!actual.isCollapsed) {
      fail("is collapsed: height is ${actual.layoutParams.height}")
    }
  }

  fun isFadedOut() = checkNotNullThen { actual ->
    if (!actual.isFadedOut) {
      fail("is faded out: visibility is ${actual.visibility}")
    }
  }

  fun isFadedIn() = checkNotNullThen { actual ->
    if (!actual.isFadedIn) {
      fail("is faded in: alpha is ${actual.alpha}, visibility is ${actual.visibility}")
    }
  }

  private fun checkNotNullThen(callback: (View) -> Unit) {
    checkNotNull(actual(), { "actual must not be null." })
    callback(actual()!!)
  }
}

object ViewAssertions {
  @JvmStatic
  fun assertThatView(view: View?): ViewSubject = assertAbout(VIEW_SUBJECT_FACTORY).that(view)
}
