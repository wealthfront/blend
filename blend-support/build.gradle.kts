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
}

android {
  compileSdkVersion(Version.targetSdkVersion)
  defaultConfig {
    minSdkVersion(Version.minSdkVersion)
    targetSdkVersion(Version.targetSdkVersion)
  }

  compileOptions {
    sourceCompatibility = Version.javaVersion
    targetCompatibility = Version.javaVersion
  }

  testOptions {
    unitTests.isIncludeAndroidResources = true
    unitTests.isReturnDefaultValues = true
  }
  namespace = "com.wealthfront.blend.support"
}

allOpen {
  annotation("com.wealthfront.ktx.Mockable")
}

apply(from = rootProject.file("gradle/gradle-mvn-push.gradle"))
