package com.wealthfront

import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import kotlin.math.absoluteValue

internal val View.isCollapsed: Boolean get() {
  return visibility == GONE || layoutParams.height == 0
}

internal val View.isExpanded: Boolean get() {
  return layoutParams.height == WRAP_CONTENT && visibility != GONE
}

internal val View.isFadedIn: Boolean get() {
  return visibility == VISIBLE && alpha.isCloseEnoughTo(1f)
}

internal val View.isFadedOut: Boolean get() {
  return visibility == INVISIBLE
}

private const val FLOAT_TOLERANCE = .01f
private fun Float?.isCloseEnoughTo(other: Float) = this != null && (this - other).absoluteValue < FLOAT_TOLERANCE
