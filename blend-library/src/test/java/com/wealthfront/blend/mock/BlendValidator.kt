package com.wealthfront.blend.mock

import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.google.common.truth.Truth.assertThat
import com.wealthfront.blend.animator.SinglePropertyAnimation
import com.wealthfront.blend.builder.SetPropertyValueAction
import com.wealthfront.blend.builder.SetVisibilityAction
import com.wealthfront.blend.properties.AdditiveProperty
import com.wealthfront.blend.properties.AdditiveViewProperties.ALPHA
import com.wealthfront.blend.properties.AdditiveViewProperties.HEIGHT

private const val TOLERANCE = 0.01f

/**
 * A validator that allows for verifying that animations were run on the [subject].
 */
class ViewBlendValidator(
  val subject: View,
  allViewAnimations: List<SinglePropertyAnimation<View>>,
  val allEndActions: Map<SinglePropertyAnimation<*>, List<() -> Unit>>,
  val isInfinite: Boolean,
  val viewsThatHaveStoppedPulsing: List<View>
) {

  val animations: List<SinglePropertyAnimation<View>> = allViewAnimations.filter { it.subject == subject }

  fun isNeverAnimated() {
    assertThat(animations).isEmpty()
  }

  fun hasPropertySetTo(property: AdditiveProperty<View>, value: Float) {
    val endValue = findEndValueForProperty(property)

    assertThat(endValue).isNotNull()
    assertThat(endValue).isWithin(TOLERANCE).of(value)
  }

  fun isExpanded() {
    val endHeight = findEndValueForProperty(HEIGHT)

    assertThat(endHeight).isNotNull()
    assertThat(endHeight).isWithin(TOLERANCE).of(WRAP_CONTENT.toFloat())
  }

  fun isCollapsed() {
    val endHeight = findEndValueForProperty(HEIGHT)
    assertThat(endHeight).isNotNull()
    assertThat(endHeight).isWithin(TOLERANCE).of(0f)
  }

  fun isFadedIn() {
    val endAlpha = findEndValueForProperty(ALPHA)
    assertThat(endAlpha).isNotNull()
    assertThat(endAlpha).isWithin(TOLERANCE).of(1f)
  }

  fun isFadedOut() {
    val endAlpha = findEndValueForProperty(ALPHA)
    assertThat(endAlpha).isNotNull()
    assertThat(endAlpha).isWithin(TOLERANCE).of(0f)

    val setVisibilityAction = allEndActions[animations.last { it.property == ALPHA } ]
        ?.mapNotNull { it as? SetVisibilityAction }
        ?.first()
    assertThat(setVisibilityAction).isNotNull()
    assertThat(setVisibilityAction!!.value).isEqualTo(INVISIBLE)
  }

  fun isPulsing() {
    val endAlpha = findEndValueForProperty(ALPHA)
    assertThat(endAlpha).isNotNull()
    assertThat(isInfinite).isTrue()
  }

  fun hasStoppedPulsing() {
    val endAlpha = findEndValueForProperty(ALPHA)
    assertThat(endAlpha).isNotNull()
    assertThat(endAlpha).isWithin(0.01f).of(1f)
    assertThat(viewsThatHaveStoppedPulsing).contains(subject)
  }

  private fun findPropertySettingEndActionsForAnimation(
    animation: SinglePropertyAnimation<View>,
    property: AdditiveProperty<View>
  ) =
      allEndActions[animation]
          ?.mapNotNull { it as? SetPropertyValueAction }
          ?.find { it.property == property }

  private fun findEndValueForProperty(property: AdditiveProperty<View>): Float? {
    val lastAnimation = animations.lastOrNull { it.property == property }
    val setPropertyEndAction = lastAnimation?.let {
      findPropertySettingEndActionsForAnimation(lastAnimation, property)
    }
    return setPropertyEndAction?.value ?: lastAnimation?.targetValue
  }
}
