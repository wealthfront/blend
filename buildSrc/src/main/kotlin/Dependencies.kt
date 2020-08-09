
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

  val dependencyAnalyzer = "com.vanniktech:gradle-dependency-graph-generator-plugin:$dependencyAnalyzerVersion"

  val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
  val appCompat = "androidx.appcompat:appcompat:$appCompatVersion"
  val ktx = "androidx.core:core-ktx:$ktxVersion"
  val constraintLayout = "androidx.constraintlayout:constraintlayout:$constraintLayoutVersion"
  val material = "com.google.android.material:material:$materialVersion"
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
  val targetSdkVersion = 28
  val buildTools = "28.0.3"

  val kotlinVersion = "1.3.31"
  val javaVersion = JavaVersion.VERSION_1_8
  val dependencyAnalyzerVersion = "0.6.0-SNAPSHOT"
  val kotlinterVersion = "1.24.0"
  val detektVersion = "1.11.0-RC2"

  val appCompatVersion = "1.1.0"
  val ktxVersion = "1.2.0"
  val constraintLayoutVersion = "1.1.3"
  val materialVersion = "1.1.0"
  val truthVersion = "0.39"
  val junitVersion = "4.12"
  val robolectricVersion = "4.3.1"
  val jsrVersion = "3.0.2"
  val testSupportVersion="1.2.0"
  val mockitoVersion = "2.23.4"
  val espressoVersion = "3.2.0"
  val androidTestExtVersion = "1.1.1"
  val javaInject = "1"
}
