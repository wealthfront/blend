package com.wealthfront.ktx

import android.view.View

internal val View.wrapContentWidth: Int get() {
  val matchParentHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(matchParentHeight, View.MeasureSpec.EXACTLY)
  val wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
  measure(wrapContentMeasureSpec, matchParentHeightMeasureSpec)
  return measuredWidth
}

internal val View.wrapContentHeight: Int get() {
  val matchParentWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(matchParentWidth, View.MeasureSpec.EXACTLY)
  val wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
  measure(matchParentWidthMeasureSpec, wrapContentMeasureSpec)
  return measuredHeight
}

internal val View.matchParentWidth: Int get() {
  return if (parent != null) {
    val parentContainer = parent as View
    parentContainer.width - parentContainer.paddingLeft - parentContainer.paddingRight
  } else {
    Int.MAX_VALUE
  }
}

internal val View.matchParentHeight: Int get() {
  return if (parent != null) {
    val parentContainer = parent as View
    parentContainer.height - parentContainer.paddingTop - parentContainer.paddingBottom
  } else {
    Int.MAX_VALUE
  }
}
