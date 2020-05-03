package com.wealthfront

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext

val application: Context
  @JvmName("application")
  get() = getApplicationContext<Application>()
