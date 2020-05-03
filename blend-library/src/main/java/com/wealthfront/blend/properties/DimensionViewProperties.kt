package com.wealthfront.blend.properties

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.wealthfront.blend.R
import com.wealthfront.ktx.matchParentHeight
import com.wealthfront.ktx.matchParentWidth
import com.wealthfront.ktx.wrapContentHeight
import com.wealthfront.ktx.wrapContentWidth
import kotlin.math.roundToInt

/**
 * Height and width properties that understand the special values of [WRAP_CONTENT] and [MATCH_PARENT] as well as
 * visibility of [GONE].
 */
object DimensionViewProperties {

  var WIDTH: AdditiveProperty<View> = object : AdditiveIntProperty<View> {
    override val id: Int = R.id.additiveWidth

    override fun setValue(subject: View, value: Float) {
      subject.layoutParams.width = value.roundToInt()
      subject.requestLayoutIfNotAlreadyInLayout()
    }

    override fun getCurrentValue(subject: View): Float =
        if (subject.visibility == GONE) {
          0f
        } else {
          subject.layoutParams.width.toFloat()
        }

    override fun getAnimationData(subject: View): AnimationData = subject.getAnimationData(id)

    override fun setUpOnAnimationQueued(subject: View) {
      if (subject.visibility == GONE) {
        setValue(subject, 0f)
        subject.visibility = VISIBLE
      } else {
        setValue(subject, getCurrentValue(subject)
            .roundToInt()
            .fromAndroidDimension(
                calculateWrapContentDimen = { subject.wrapContentWidth },
                calculateMatchParentDimen = { subject.matchParentWidth })
            .toFloat())
      }
    }
  }

  var HEIGHT: AdditiveProperty<View> = object : AdditiveIntProperty<View> {
    override val id: Int = R.id.additiveHeight

    override fun setValue(subject: View, value: Float) {
      subject.layoutParams.height = value.roundToInt()
      subject.requestLayoutIfNotAlreadyInLayout()
    }

    override fun getCurrentValue(subject: View): Float =
        if (subject.visibility == GONE) {
          0f
        } else {
          subject.layoutParams.height.toFloat()
        }

    override fun getAnimationData(subject: View): AnimationData = subject.getAnimationData(id)

    override fun setUpOnAnimationQueued(subject: View) {
      if (subject.visibility == GONE) {
        setValue(subject, 0f)
        subject.visibility = VISIBLE
      } else {
        setValue(subject, getCurrentValue(subject)
            .roundToInt()
            .fromAndroidDimension(
                calculateWrapContentDimen = { subject.wrapContentHeight },
                calculateMatchParentDimen = { subject.matchParentHeight })
            .toFloat())
      }
    }
  }
}

private fun Int.fromAndroidDimension(
  calculateWrapContentDimen: () -> Int,
  calculateMatchParentDimen: () -> Int
) = when (this) {
  WRAP_CONTENT -> calculateWrapContentDimen()
  MATCH_PARENT -> calculateMatchParentDimen()
  else -> this
}
