plugins {
  id("com.android.library")
  id("kotlin-android")
  id("kotlin-allopen")
}

dependencies {
  implementation(project(":blend-library"))
  implementation(project(":support"))
  implementation(Libs.appCompat)
  implementation(Libs.kotlinStdLib)
  implementation(Libs.inject)
  implementation(Libs.jsr305)

  implementation(Libs.mockitoCore)
  implementation(Libs.junit)
  implementation(Libs.testCore)
  implementation(Libs.truth)
  implementation(Libs.robolectric) {
    exclude(group = "commons-logging", module = "commons-logging")
    exclude(group = "org.apache.httpcomponents", module = "httpclient")
  }
}

android {
  compileSdkVersion(Version.targetSdkVersion)
  defaultConfig {
    minSdkVersion(Version.minSdkVersion)
    targetSdkVersion(Version.targetSdkVersion)

    javaCompileOptions {
      annotationProcessorOptions {
        includeCompileClasspath = true
      }
    }
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
