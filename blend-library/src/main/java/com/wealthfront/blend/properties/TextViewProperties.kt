package com.wealthfront.blend.properties

import android.widget.TextView
import com.wealthfront.blend.R

/**
 * Properties specific to [TextView]s.
 */
object TextViewProperties {

  @JvmField
  var TEXT_COLOR: AdditiveProperty<TextView> =
      makeAdditiveViewProperty(
          id = R.id.additiveTextColor,
          get = { subject -> subject.currentTextColor.toFloat() },
          set = { subject, value -> subject.setTextColor(value.toInt()) },
          interpolateAction = { startValue, endValue, timeFraction, _ ->
            blendColor(startValue, endValue, timeFraction)
          }
      )
}
