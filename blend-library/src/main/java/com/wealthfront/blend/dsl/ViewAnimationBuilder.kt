package com.wealthfront.blend.dsl

import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat.getColor
import com.wealthfront.blend.animator.BlendableAnimator
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
 * An [AnimationBuilder] for [View]s and their subclasses.
 */
open class ViewAnimationBuilder<Subject : View>(
  override val currentSubjects: List<Subject>,
  override var currentAnimator: BlendableAnimator
) : AnimationBuilder<Subject> {

  /**
   * Add a width animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun width(targetValue: Int) = currentSubjects.forEach { currentView ->
    when (targetValue) {
      WRAP_CONTENT -> {
        addAnimation(currentView, WIDTH, currentView.wrapContentWidth.toFloat())
        doOnFinishedUnlessLastAnimationInterrupted(
            SetPropertyValueAction(currentView, WIDTH, WRAP_CONTENT.toFloat()))
      }
      MATCH_PARENT -> {
        addAnimation(currentView, WIDTH, currentView.matchParentWidth.toFloat())
        doOnFinishedUnlessLastAnimationInterrupted(
            SetPropertyValueAction(currentView, WIDTH, MATCH_PARENT.toFloat()))
      }
      else -> {
        addAnimation(currentView, WIDTH, targetValue.toFloat())
      }
    }
  }

  /**
   * Add a height animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun height(targetValue: Int) = currentSubjects.forEach { currentView ->
    when (targetValue) {
      WRAP_CONTENT -> {
        addAnimation(currentView, HEIGHT, currentView.wrapContentHeight.toFloat())
        doOnFinishedUnlessLastAnimationInterrupted(
            SetPropertyValueAction(currentView, HEIGHT, WRAP_CONTENT.toFloat()))
      }
      MATCH_PARENT -> {
        addAnimation(currentView, HEIGHT, currentView.matchParentHeight.toFloat())
        doOnFinishedUnlessLastAnimationInterrupted(
            SetPropertyValueAction(currentView, HEIGHT, MATCH_PARENT.toFloat()))
      }
      else -> {
        addAnimation(currentView, HEIGHT, targetValue.toFloat())
      }
    }
  }

  /**
   * Add a height animation for all [currentSubjects] with a final value of [WRAP_CONTENT].
   */
  fun expand() = height(WRAP_CONTENT)

  /**
   * Add a height animation for all [currentSubjects] with a final value of `0`.
   */
  fun collapse() = height(0)

  /**
   * Add an animation for any height change that happens in [idempotentAction], e.g. adding or removing child views.
   *
   * This method will run [idempotentAction] at least once, and potentially multiple times.
   */
  fun expandAfter(idempotentAction: () -> Unit) = apply {
    currentSubjects.forEach { view ->
      currentAnimator.doBeforeStart(HoldHeightConstantAction(view, idempotentAction))
    }
    height(WRAP_CONTENT)
  }

  /**
   * Add an alpha animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun alpha(targetValue: Float) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, ALPHA, targetValue)
    if (targetValue == 0f) {
      currentAnimator.doOnFinishedUnlessLastAnimationInterrupted(
          SetVisibilityAction(
              currentView,
              INVISIBLE))
    }
  }

  /**
   * Add an alpha animation for all [currentSubjects] with a final value of 0, or fully transparent.
   */
  fun fadeOut() = alpha(0f)

  /**
   * Add an alpha animation for all [currentSubjects] with a final value of 1, or fully opaque.
   */
  fun fadeIn() = alpha(1f)

  /**
   * Add an animation to fade all [currentSubjects] in and all [others] out simultaneously.
   */
  fun crossfadeWith(vararg others: View) {
    fadeOut()
    target(others.toList()) { fadeIn() }
  }

  /**
   * Add a translationX animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun translationX(targetValue: Float) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, TRANSLATION_X, targetValue)
  }

  /**
   * Add a translationY animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun translationY(targetValue: Float) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, TRANSLATION_Y, targetValue)
  }

  /**
   * Add a translationZ animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun translationZ(targetValue: Float) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, TRANSLATION_Z, targetValue)
  }

  /**
   * Add an x animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun x(targetValue: Float) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, X, targetValue)
  }

  /**
   * Add a y animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun y(targetValue: Float) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, Y, targetValue)
  }

  /**
   * Add a z animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun z(targetValue: Float) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, Z, targetValue)
  }

  /**
   * Add a rotation animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun rotation(targetValue: Float) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, ROTATION, targetValue)
  }

  /**
   * Add a rotationX animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun rotationX(targetValue: Float) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, ROTATION_X, targetValue)
  }

  /**
   * Add a rotationY animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun rotationY(targetValue: Float) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, ROTATION_Y, targetValue)
  }

  /**
   * Add a scaleX and a scaleY animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun scale(targetValue: Float) = apply {
    scaleX(targetValue)
    scaleY(targetValue)
  }

  /**
   * Add a scaleX animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun scaleX(targetValue: Float) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, SCALE_X, targetValue)
  }

  /**
   * Add a scaleY animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun scaleY(targetValue: Float) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, SCALE_Y, targetValue)
  }

  /**
   * Add an elevation animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun elevation(targetValue: Float) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, ELEVATION, targetValue)
  }

  /**
   * Add an animation for padding on all sides for all [currentSubjects] with a final value of [targetValue].
   */
  fun padding(targetValue: Int) = apply {
    paddingLeft(targetValue)
    paddingRight(targetValue)
    paddingTop(targetValue)
    paddingBottom(targetValue)
  }

  /**
   * Add a paddingLeft animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun paddingLeft(targetValue: Int) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, PADDING_LEFT, targetValue.toFloat())
  }

  /**
   * Add a paddingRight animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun paddingRight(targetValue: Int) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, PADDING_RIGHT, targetValue.toFloat())
  }

  /**
   * Add a paddingTop animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun paddingTop(targetValue: Int) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, PADDING_TOP, targetValue.toFloat())
  }

  /**
   * Add a paddingBottom animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun paddingBottom(targetValue: Int) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, PADDING_BOTTOM, targetValue.toFloat())
  }

  /**
   * Add an animation for margins on all sides for all [currentSubjects] with a final value of [targetValue].
   */
  fun margin(targetValue: Int) = apply {
    marginLeft(targetValue)
    marginRight(targetValue)
    marginTop(targetValue)
    marginBottom(targetValue)
  }

  /**
   * Add a marginLeft animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun marginLeft(targetValue: Int) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, MARGIN_LEFT, targetValue.toFloat())
  }

  /**
   * Add a marginRight animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun marginRight(targetValue: Int) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, MARGIN_RIGHT, targetValue.toFloat())
  }

  /**
   * Add a marginTop animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun marginTop(targetValue: Int) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, MARGIN_TOP, targetValue.toFloat())
  }

  /**
   * Add a marginBottom animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun marginBottom(targetValue: Int) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, MARGIN_BOTTOM, targetValue.toFloat())
  }

  /**
   * Add a backgroundColor animation for all [currentSubjects] with a final value of the color resolved by
   * [targetValueRes].
   */
  fun backgroundColor(@ColorRes targetValueRes: Int) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, BACKGROUND_COLOR, getColor(currentView.context, targetValueRes).toFloat())
  }

  /**
   * Add a backgroundColor animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun backgroundColorValue(@ColorInt targetValue: Int) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, BACKGROUND_COLOR, targetValue.toFloat())
  }

  /**
   * Add a scrollY animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun scrollY(targetValue: Int) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, SCROLL_Y, targetValue.toFloat())
  }

  /**
   * Add a scrollX animation for all [currentSubjects] with a final value of [targetValue].
   */
  fun scrollX(targetValue: Int) = currentSubjects.forEach { currentView ->
    addAnimation(currentView, SCROLL_X, targetValue.toFloat())
  }

  private fun <T : View> target(views: List<T>, action: ViewAnimationBuilder<T>.() -> Unit) {
    ViewAnimationBuilder(views, currentAnimator).action()
  }
}