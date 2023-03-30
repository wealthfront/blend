plugins {
  id("com.android.library")
  id("kotlin-android")
  id("kotlin-allopen")
}

dependencies {
  implementation(project(":blend-library"))
  implementation(project(":blend-support"))
  implementation(Libs.appCompat)
  implementation(Libs.kotlinStdLib)
  implementation(Libs.inject)
  implementation(Libs.jsr305)

  implementation(Libs.truth)
  testImplementation(Libs.testCore)
  testImplementation(Libs.junit)
  testImplementation(Libs.mockitoCore)
  testImplementation(Libs.robolectric) {
    exclude(group = "commons-logging", module = "commons-logging")
    exclude(group = "org.apache.httpcomponents", module = "httpclient")
  }

  testImplementation(Libs.testCore)
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
  namespace = "com.wealthfront.blend.test"
}

allOpen {
  annotation("com.wealthfront.ktx.Mockable")
}

apply(from = rootProject.file("gradle/gradle-mvn-push.gradle"))
