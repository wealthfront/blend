package com.wealthfront.blend.sample

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext

val application: Context
  @JvmName("application")
  get() = getApplicationContext<Application>()
