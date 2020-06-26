package com.wealthfront.blend.dsl

import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat.getColor
import com.wealthfront.blend.properties.ProgressBarProperties.PROGRESS
import com.wealthfront.blend.properties.TextViewProperties.TEXT_COLOR

/**
 * Add a textColor animation for all [AnimationBuilder.currentSubjects] with a final value of [targetRes].
 */
fun AnimationBuilder<TextView>.textColor(@ColorRes targetRes: Int) =
  customProperty(getColor(currentSubjects[0].context, targetRes).toFloat(), TEXT_COLOR)

/**
 * Add a textColor animation for all [AnimationBuilder.currentSubjects] with a final value of [target].
 */
fun AnimationBuilder<TextView>.textColorValue(@ColorInt target: Int) =
  customProperty(target.toFloat(), TEXT_COLOR)


/**
 * Add a progress animation for all [AnimationBuilder.currentSubjects] with a final value of [target].
 */
fun AnimationBuilder<ProgressBar>.progress(target: Int) =
  customProperty(target.toFloat(), PROGRESS)
