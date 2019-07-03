package com.wealthfront.blend.properties

import android.widget.ProgressBar
import com.wealthfront.blend.R

/**
 * Properties specific to [ProgressBar]s.
 */
object ProgressBarProperties {

  @JvmField
  var PROGRESS: AdditiveProperty<ProgressBar> =
      makeAdditiveIntViewProperty(
          id = R.id.additiveProgress,
          get = { subject -> subject.progress.toFloat() },
          set = { subject, value -> subject.progress = value.toInt() }
      )
}
