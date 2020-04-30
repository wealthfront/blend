package com.wealthfront.blend.sample

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext

object ThemedApplicationProvider {
  @JvmStatic
  fun getThemedApplicationContext(): Context {
    val application = getApplicationContext<Application>()
    application.setTheme(R.style.AppTheme)
    return application
  }

  @JvmStatic
  val application: Context
    @JvmName("application")
    get() = getThemedApplicationContext()
}
