package com.wealthfront

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext

object ThemedApplicationProvider {
  @JvmStatic
  fun getApplicationContext(): Context {
    val application = getApplicationContext<Application>()
    return application
  }

  @JvmStatic
  val application: Context
    @JvmName("application")
    get() = getApplicationContext()
}
