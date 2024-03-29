import Version.androidTestExtVersion
import Version.appCompatVersion
import Version.constraintLayoutVersion
import Version.dependencyAnalyzerVersion
import Version.espressoVersion
import Version.javaInject
import Version.jsrVersion
import Version.junitVersion
import Version.kotlinVersion
import Version.kotlinterVersion
import Version.ktxVersion
import Version.leakCanaryVersion
import Version.materialVersion
import Version.mockitoVersion
import Version.robolectricVersion
import Version.testSupportVersion
import Version.truthVersion
import org.gradle.api.JavaVersion

object Libs {

  val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
  val kotlinterGradle = "org.jmailen.gradle:kotlinter-gradle:$kotlinterVersion"
  val kotlinAllOpen = "org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion"

  val dependencyAnalyzer =
    "com.vanniktech:gradle-dependency-graph-generator-plugin:$dependencyAnalyzerVersion"

  val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
  val appCompat = "androidx.appcompat:appcompat:$appCompatVersion"
  val ktx = "androidx.core:core-ktx:$ktxVersion"
  val constraintLayout = "androidx.constraintlayout:constraintlayout:$constraintLayoutVersion"
  val material = "com.google.android.material:material:$materialVersion"
  val leakCanary = "com.squareup.leakcanary:leakcanary-android:$leakCanaryVersion"
  val inject = "javax.inject:javax.inject:$javaInject"
  val jsr305 = "com.google.code.findbugs:jsr305:$jsrVersion"
  val testCore = "androidx.test:core:$testSupportVersion"
  val mockitoCore = "org.mockito:mockito-core:$mockitoVersion"
  val junit = "junit:junit:$junitVersion"
  val truth = "com.google.truth:truth:$truthVersion"
  val robolectric = "org.robolectric:robolectric:$robolectricVersion"
  val espresso = "androidx.test.espresso:espresso-core:$espressoVersion"
  val androidTestExt = "androidx.test.ext:junit:$androidTestExtVersion"
}

object Version {

  val minSdkVersion = 21
  val targetSdkVersion = 30

  val kotlinVersion = "1.7.21"
  val javaVersion = JavaVersion.VERSION_1_8
  val dependencyAnalyzerVersion = "0.8.0"
  val kotlinterVersion = "3.13.0"
  val detektVersion = "1.22.0"

  val appCompatVersion = "1.1.0"
  val ktxVersion = "1.2.0"
  val constraintLayoutVersion = "1.1.3"
  val materialVersion = "1.1.0"
  val leakCanaryVersion = "2.12"
  val truthVersion = "0.39"
  val junitVersion = "4.13.2"
  val robolectricVersion = "4.9.2"
  val jsrVersion = "3.0.2"
  val testSupportVersion = "1.4.0"
  val mockitoVersion = "2.23.4"
  val espressoVersion = "3.5.1"
  val androidTestExtVersion = "1.5.0"
  val javaInject = "1"
}
