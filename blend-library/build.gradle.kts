plugins {
  id("com.android.library")
  id("kotlin-android")
  id("kotlin-allopen")
}

dependencies {
  implementation(Libs.appCompat)
  implementation(Libs.kotlinStdLib)
  implementation(Libs.inject)
  implementation(Libs.jsr305)

  testImplementation(Libs.mockitoCore)
  testImplementation(Libs.junit)
  testImplementation(Libs.truth)
  testImplementation(Libs.robolectric) {
    exclude(group = "commons-logging", module = "commons-logging")
    exclude(group = "org.apache.httpcomponents", module = "httpclient")
  }
}

android {
  compileSdkVersion(Version.targetSdkVersion)
  defaultConfig {
    minSdkVersion(Version.minSdkVersion)
    targetSdkVersion(Version.targetSdkVersion)
  }

  compileOptions {
    setSourceCompatibility(Version.javaVersion)
    setTargetCompatibility(Version.javaVersion)
  }

  testOptions {
    unitTests.isIncludeAndroidResources = true
    unitTests.isReturnDefaultValues = true
  }
}

allOpen {
  annotation("com.wealthfront.ktx.Mockable")
}

apply(from = rootProject.file("gradle/gradle-mvn-push.gradle"))
