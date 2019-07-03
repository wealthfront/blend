package com.wealthfront.blend.dsl

import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat.getColor
import com.wealthfront.blend.animator.BlendableAnimator
import com.wealthfront.blend.properties.ProgressBarProperties.PROGRESS
import com.wealthfront.blend.properties.TextViewProperties.TEXT_COLOR

/**
 * A [ViewAnimationBuilder] that knows how to animate [TextView]-specific properties.
 */
open class TextViewAnimationBuilder<Subject : TextView>(
  currentSubjects: List<Subject>,
  currentAnimator: BlendableAnimator
) : ViewAnimationBuilder<Subject>(currentSubjects, currentAnimator) {

  fun textColor(@ColorRes targetRes: Int) {
    val targetColor = getColor(currentSubjects[0].context, targetRes)
    currentSubjects.forEach { textView ->
      currentAnimator.addAnimation(textView, TEXT_COLOR, targetColor.toFloat())
    }
  }
}

/**
 * A [ViewAnimationBuilder] that knows how to animate [ProgressBar]-specific properties.
 */
open class ProgressBarAnimationBuilder<Subject : ProgressBar>(
  currentSubjects: List<Subject>,
  currentAnimator: BlendableAnimator
) : ViewAnimationBuilder<Subject>(currentSubjects, currentAnimator) {

  fun progress(target: Int) {
    currentSubjects.forEach { progressBar ->
      currentAnimator.addAnimation(progressBar, PROGRESS, target.toFloat())
    }
  }
}
