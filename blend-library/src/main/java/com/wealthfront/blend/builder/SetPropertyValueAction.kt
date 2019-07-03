package com.wealthfront.blend.builder

import android.view.View
import com.wealthfront.blend.properties.AdditiveProperty

open class SetPropertyValueAction(val subject: View, val property: AdditiveProperty<View>, val value: Float) : () -> Unit {
  override fun invoke() {
    property.setValue(subject, value)
  }
}

open class SetVisibilityAction(val subject: View, val value: Int) : () -> Unit {
  override fun invoke() {
    subject.visibility = value
  }
}

open class HoldHeightConstantAction(val subject: View, val idempotentAction: () -> Unit) : () -> Unit {
  override fun invoke() {
    val oldHeight = subject.height
    idempotentAction()
    subject.layoutParams.height = oldHeight
  }
}
