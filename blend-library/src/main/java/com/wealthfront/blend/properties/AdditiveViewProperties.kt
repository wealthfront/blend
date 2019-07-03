package com.wealthfront.blend.properties

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup.MarginLayoutParams
import com.wealthfront.blend.R
import kotlin.math.roundToInt

/**
 * A standard collection of [AdditiveProperty]s for [View]s.
 *
 * Includes all of [View]'s built-in properties and a few extras.
 */
object AdditiveViewProperties {
  @JvmField
  val ALPHA = wrapViewProperty(View.ALPHA,
      R.id.additiveAlpha,
      setUpAction = { subject ->
        if (subject.visibility == INVISIBLE) {
          subject.alpha = 0f
          subject.visibility = VISIBLE
        }
      })

  @JvmField
  val TRANSLATION_X =
      wrapViewProperty(View.TRANSLATION_X, R.id.additiveTranslationX)

  @JvmField
  val TRANSLATION_Y =
      wrapViewProperty(View.TRANSLATION_Y, R.id.additiveTranslationY)

  @JvmField
  val TRANSLATION_Z =
      wrapViewProperty(View.TRANSLATION_Z, R.id.additiveTranslationZ)

  @JvmField
  val X = wrapViewProperty(View.X, R.id.additiveX)

  @JvmField
  val Y = wrapViewProperty(View.Y, R.id.additiveY)

  @JvmField
  val Z = wrapViewProperty(View.Z, R.id.additiveZ)

  @JvmField
  val ROTATION = wrapViewProperty(View.ROTATION, R.id.additiveRotation)

  @JvmField
  val ROTATION_X = wrapViewProperty(View.ROTATION_X, R.id.additiveRotationX)

  @JvmField
  val ROTATION_Y = wrapViewProperty(View.ROTATION_Y, R.id.additiveRotationY)

  @JvmField
  val SCALE_X = wrapViewProperty(View.SCALE_X, R.id.additiveScaleX)

  @JvmField
  val SCALE_Y = wrapViewProperty(View.SCALE_Y, R.id.additiveScaleY)

  @JvmField
  var ELEVATION: AdditiveProperty<View> =
      makeAdditiveViewProperty(
          id = R.id.additiveElevation,
          get = { subject -> subject.elevation },
          set = { subject, value -> subject.elevation = value }
      )

  @JvmField
  var WIDTH: AdditiveProperty<View> =
      DimensionViewProperties.WIDTH

  @JvmField
  var HEIGHT: AdditiveProperty<View> =
      DimensionViewProperties.HEIGHT

  @SuppressLint("UseCustomSetPadding")
  @JvmField
  var PADDING_LEFT: AdditiveProperty<View> =
      makeAdditiveIntViewProperty(
          id = R.id.additivePaddingLeft,
          get = { subject -> subject.paddingLeft.toFloat() },
          set = { subject, value ->
            subject.setPadding(value.roundToInt(), subject.paddingTop, subject.paddingRight, subject.paddingBottom)
            subject.requestLayoutIfNotAlreadyInLayout()
          }
      )

  @SuppressLint("UseCustomSetPadding")
  @JvmField
  var PADDING_RIGHT: AdditiveProperty<View> =
      makeAdditiveIntViewProperty(
          id = R.id.additivePaddingRight,
          get = { subject -> subject.paddingRight.toFloat() },
          set = { subject, value ->
            subject.setPadding(subject.paddingLeft, subject.paddingTop, value.toInt(), subject.paddingBottom)
            subject.requestLayoutIfNotAlreadyInLayout()
          }
      )

  @SuppressLint("UseCustomSetPadding")
  @JvmField
  var PADDING_TOP: AdditiveProperty<View> =
      makeAdditiveIntViewProperty(
          id = R.id.additivePaddingTop,
          get = { subject -> subject.paddingTop.toFloat() },
          set = { subject, value ->
            subject.setPadding(subject.paddingLeft, value.toInt(), subject.paddingRight, subject.paddingBottom)
            subject.requestLayoutIfNotAlreadyInLayout()
          }
      )

  @SuppressLint("UseCustomSetPadding")
  @JvmField
  var PADDING_BOTTOM: AdditiveProperty<View> =
      makeAdditiveIntViewProperty(
          id = R.id.additivePaddingBottom,
          get = { subject -> subject.paddingBottom.toFloat() },
          set = { subject, value ->
            subject.setPadding(subject.paddingLeft, subject.paddingTop, subject.paddingRight, value.toInt())
            subject.requestLayoutIfNotAlreadyInLayout()
          }
      )

  @JvmField
  var MARGIN_LEFT: AdditiveProperty<View> =
      makeAdditiveIntViewProperty(
          id = R.id.additiveMarginLeft,
          get = { subject -> (subject.layoutParams as MarginLayoutParams).leftMargin.toFloat() },
          set = { subject, value ->
            (subject.layoutParams as MarginLayoutParams).leftMargin = value.toInt()
            subject.requestLayoutIfNotAlreadyInLayout()
          }
      )

  @JvmField
  var MARGIN_RIGHT: AdditiveProperty<View> =
      makeAdditiveIntViewProperty(
          id = R.id.additiveMarginRight,
          get = { subject -> (subject.layoutParams as MarginLayoutParams).rightMargin.toFloat() },
          set = { subject, value ->
            (subject.layoutParams as MarginLayoutParams).rightMargin = value.toInt()
            subject.requestLayoutIfNotAlreadyInLayout()
          }
      )

  @JvmField
  var MARGIN_TOP: AdditiveProperty<View> =
      makeAdditiveIntViewProperty(
          id = R.id.additiveMarginTop,
          get = { subject -> (subject.layoutParams as MarginLayoutParams).topMargin.toFloat() },
          set = { subject, value ->
            (subject.layoutParams as MarginLayoutParams).topMargin = value.toInt()
            subject.requestLayoutIfNotAlreadyInLayout()
          }
      )

  @JvmField
  var MARGIN_BOTTOM: AdditiveProperty<View> =
      makeAdditiveIntViewProperty(
          id = R.id.additiveMarginBottom,
          get = { subject -> (subject.layoutParams as MarginLayoutParams).bottomMargin.toFloat() },
          set = { subject, value ->
            (subject.layoutParams as MarginLayoutParams).bottomMargin = value.toInt()
            subject.requestLayoutIfNotAlreadyInLayout()
          }
      )

  @JvmField
  var BACKGROUND_COLOR: AdditiveProperty<View> =
      makeAdditiveViewProperty(
          id = R.id.additiveBackgroundColor,
          get = { subject ->
            (subject.background as? ColorDrawable)?.color?.toFloat() ?: 0f
          },
          set = { subject, value -> subject.setBackgroundColor(value.toInt()) },
          interpolateAction = { startValue, endValue, timeFraction, _ ->
            blendColor(startValue, endValue, timeFraction)
          }
      )

  @JvmField
  var SCROLL_Y: AdditiveProperty<View> =
      makeAdditiveIntViewProperty(
          id = R.id.additiveScrollY,
          get = { subject -> subject.scrollY.toFloat() },
          set = { subject, value -> subject.scrollY = value.toInt() }
      )

  @JvmField
  var SCROLL_X: AdditiveProperty<View> =
      makeAdditiveIntViewProperty(
          id = R.id.additiveScrollX,
          get = { subject -> subject.scrollX.toFloat() },
          set = { subject, value -> subject.scrollX = value.toInt() }
      )
}
