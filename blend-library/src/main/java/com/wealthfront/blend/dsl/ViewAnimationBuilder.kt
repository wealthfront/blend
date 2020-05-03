package com.wealthfront.blend.dsl

import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat.getColor
import com.wealthfront.blend.builder.HoldHeightConstantAction
import com.wealthfront.blend.builder.SetPropertyValueAction
import com.wealthfront.blend.builder.SetVisibilityAction
import com.wealthfront.blend.properties.AdditiveViewProperties.ALPHA
import com.wealthfront.blend.properties.AdditiveViewProperties.BACKGROUND_COLOR
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
import com.wealthfront.blend.properties.AdditiveViewProperties.SCALE_X
import com.wealthfront.blend.properties.AdditiveViewProperties.SCALE_Y
import com.wealthfront.blend.properties.AdditiveViewProperties.SCROLL_X
import com.wealthfront.blend.properties.AdditiveViewProperties.SCROLL_Y
import com.wealthfront.blend.properties.AdditiveViewProperties.TRANSLATION_X
import com.wealthfront.blend.properties.AdditiveViewProperties.TRANSLATION_Y
import com.wealthfront.blend.properties.AdditiveViewProperties.TRANSLATION_Z
import com.wealthfront.blend.properties.AdditiveViewProperties.WIDTH
import com.wealthfront.blend.properties.AdditiveViewProperties.X
import com.wealthfront.blend.properties.AdditiveViewProperties.Y
import com.wealthfront.blend.properties.AdditiveViewProperties.Z
import com.wealthfront.ktx.matchParentHeight
import com.wealthfront.ktx.matchParentWidth
import com.wealthfront.ktx.wrapContentHeight
import com.wealthfront.ktx.wrapContentWidth

/**
 * Add a width animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.width(targetValue: Int) = currentSubjects.forEach { currentView ->
  when (targetValue) {
    WRAP_CONTENT -> {
      addAnimation(currentView.wrapContentWidth.toFloat(), WIDTH, currentView)
      doOnFinishedUnlessLastAnimationInterrupted(
          SetPropertyValueAction(currentView, WIDTH, WRAP_CONTENT.toFloat()))
    }
    MATCH_PARENT -> {
      addAnimation(currentView.matchParentWidth.toFloat(), WIDTH, currentView)
      doOnFinishedUnlessLastAnimationInterrupted(
          SetPropertyValueAction(currentView, WIDTH, MATCH_PARENT.toFloat()))
    }
    else -> {
      addAnimation(targetValue.toFloat(), WIDTH, currentView)
    }
  }
}

/**
 * Add a height animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.height(targetValue: Int) = currentSubjects.forEach { currentView ->
  when (targetValue) {
    WRAP_CONTENT -> {
      addAnimation(currentView.wrapContentHeight.toFloat(), HEIGHT, currentView)
      doOnFinishedUnlessLastAnimationInterrupted(
          SetPropertyValueAction(currentView, HEIGHT, WRAP_CONTENT.toFloat()))
    }
    MATCH_PARENT -> {
      addAnimation(currentView.matchParentHeight.toFloat(), HEIGHT, currentView)
      doOnFinishedUnlessLastAnimationInterrupted(
          SetPropertyValueAction(currentView, HEIGHT, MATCH_PARENT.toFloat()))
    }
    else -> {
      addAnimation(targetValue.toFloat(), HEIGHT, currentView)
    }
  }
}

/**
 * Add a height animation for all [AnimationBuilder.currentSubjects] with a final value of [WRAP_CONTENT].
 */
fun AnimationBuilder<View>.expand() = height(WRAP_CONTENT)

/**
 * Add a height animation for all [AnimationBuilder.currentSubjects] with a final value of `0`.
 */
fun AnimationBuilder<View>.collapse() = height(0)

/**
 * Add an animation for any height change that happens in [idempotentAction], e.g. adding or removing child views.
 *
 * This method will run [idempotentAction] at least once, and potentially multiple times.
 */
fun AnimationBuilder<View>.expandAfter(idempotentAction: () -> Unit) {
  currentSubjects.forEach { view ->
    currentAnimator.doBeforeStart(HoldHeightConstantAction(view, idempotentAction))
  }
  height(WRAP_CONTENT)
}

/**
 * Add an alpha animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.alpha(targetValue: Float) = currentSubjects.forEach { currentView ->
  addAnimation(targetValue, ALPHA, currentView)
  if (targetValue == 0f) {
    currentAnimator.doOnFinishedUnlessLastAnimationInterrupted(SetVisibilityAction(currentView, INVISIBLE))
  }
}

/**
 * Add an alpha animation for all [AnimationBuilder.currentSubjects] with a final value of 0, or fully transparent.
 */
fun AnimationBuilder<View>.fadeOut() = alpha(0f)

/**
 * Add an alpha animation for all [AnimationBuilder.currentSubjects] with a final value of 1, or fully opaque.
 */
fun AnimationBuilder<View>.fadeIn() = alpha(1f)

/**
 * Add an animation to fade all [AnimationBuilder.currentSubjects] in and all [others] out simultaneously.
 */
fun AnimationBuilder<View>.crossfadeWith(vararg others: View) {
  fadeOut()
  target(others.toList()) { fadeIn() }
}

/**
 * Add a translationX animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.translationX(targetValue: Float) = customProperty(targetValue, TRANSLATION_X)

/**
 * Add a translationY animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.translationY(targetValue: Float) = customProperty(targetValue, TRANSLATION_Y)

/**
 * Add a translationZ animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.translationZ(targetValue: Float) = customProperty(targetValue, TRANSLATION_Z)

/**
 * Add an x animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.x(targetValue: Float) = customProperty(targetValue, X)

/**
 * Add a y animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.y(targetValue: Float) = customProperty(targetValue, Y)

/**
 * Add a z animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.z(targetValue: Float) = customProperty(targetValue, Z)

/**
 * Add a rotation animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.rotation(targetValue: Float) = customProperty(targetValue, ROTATION)

/**
 * Add a rotationX animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.rotationX(targetValue: Float) = customProperty(targetValue, ROTATION_X)

/**
 * Add a rotationY animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.rotationY(targetValue: Float) = customProperty(targetValue, ROTATION_Y)

/**
 * Add a scaleX and a scaleY animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.scale(targetValue: Float) {
  scaleX(targetValue)
  scaleY(targetValue)
}

/**
 * Add a scaleX animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.scaleX(targetValue: Float) = customProperty(targetValue, SCALE_X)

/**
 * Add a scaleY animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.scaleY(targetValue: Float) = customProperty(targetValue, SCALE_Y)

/**
 * Add an elevation animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.elevation(targetValue: Float) = customProperty(targetValue, ELEVATION)

/**
 * Add an animation for padding on all sides for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.padding(targetValue: Int) {
  paddingLeft(targetValue)
  paddingRight(targetValue)
  paddingTop(targetValue)
  paddingBottom(targetValue)
}

/**
 * Add a paddingLeft animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.paddingLeft(targetValue: Int) = customProperty(targetValue.toFloat(), PADDING_LEFT)

/**
 * Add a paddingRight animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.paddingRight(targetValue: Int) = customProperty(targetValue.toFloat(), PADDING_RIGHT)

/**
 * Add a paddingTop animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.paddingTop(targetValue: Int) = customProperty(targetValue.toFloat(), PADDING_TOP)

/**
 * Add a paddingBottom animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.paddingBottom(targetValue: Int) = customProperty(targetValue.toFloat(), PADDING_BOTTOM)

/**
 * Add an animation for margins on all sides for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.margin(targetValue: Int) {
  marginLeft(targetValue)
  marginRight(targetValue)
  marginTop(targetValue)
  marginBottom(targetValue)
}

/**
 * Add a marginLeft animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.marginLeft(targetValue: Int) = customProperty(targetValue.toFloat(), MARGIN_LEFT)

/**
 * Add a marginRight animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.marginRight(targetValue: Int) = customProperty(targetValue.toFloat(), MARGIN_RIGHT)

/**
 * Add a marginTop animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.marginTop(targetValue: Int) = customProperty(targetValue.toFloat(), MARGIN_TOP)

/**
 * Add a marginBottom animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.marginBottom(targetValue: Int) = customProperty(targetValue.toFloat(), MARGIN_BOTTOM)

/**
 * Add a backgroundColor animation for all [AnimationBuilder.currentSubjects] with a final value of the color resolved by
 * [targetValueRes].
 */
fun AnimationBuilder<View>.backgroundColor(@ColorRes targetValueRes: Int) =
  customProperty(getColor(currentSubjects[0].context, targetValueRes).toFloat(), BACKGROUND_COLOR)

/**
 * Add a backgroundColor animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.backgroundColorValue(@ColorInt targetValue: Int) = customProperty(targetValue.toFloat(), BACKGROUND_COLOR)

/**
 * Add a scrollY animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.scrollY(targetValue: Int) = customProperty(targetValue.toFloat(), SCROLL_Y)

/**
 * Add a scrollX animation for all [AnimationBuilder.currentSubjects] with a final value of [targetValue].
 */
fun AnimationBuilder<View>.scrollX(targetValue: Int) = customProperty(targetValue.toFloat(), SCROLL_X)

private fun <T : View> AnimationBuilder<T>.target(views: List<T>, action: AnimationBuilder<T>.() -> Unit) {
  AnimationBuilder(views, currentAnimator).action()
}
