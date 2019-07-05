
import Version.appCompatVersion
import Version.dependencyAnalyzerVersion
import Version.javaInject
import Version.jsrVersion
import Version.junitVersion
import Version.kotlinVersion
import Version.kotlinterVersion
import Version.mockitoVersion
import Version.robolectricVersion
import Version.truthVersion
import org.gradle.api.JavaVersion

object Libs {

  val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
  val kotlinterGradle = "org.jmailen.gradle:kotlinter-gradle:$kotlinterVersion"
  val kotlinAllOpen = "org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion"

  val dependencyAnalyzer = "com.vanniktech:gradle-dependency-graph-generator-plugin:$dependencyAnalyzerVersion"

  val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
  val appCompat = "androidx.appcompat:appcompat:$appCompatVersion"
  val inject = "javax.inject:javax.inject:$javaInject"
  val jsr305 = "com.google.code.findbugs:jsr305:$jsrVersion"
  val mockitoCore = "org.mockito:mockito-core:$mockitoVersion"
  val junit = "junit:junit:$junitVersion"
  val truth = "com.google.truth:truth:$truthVersion"
  val robolectric = "org.robolectric:robolectric:$robolectricVersion"
}

object Version {

  val minSdkVersion = 21
  val targetSdkVersion = 28
  val buildTools = "28.0.3"

  val kotlinVersion = "1.3.31"
  val javaVersion = JavaVersion.VERSION_1_8
  val dependencyAnalyzerVersion = "0.6.0-SNAPSHOT"
  val kotlinterVersion = "1.24.0"
  val detektVersion = "1.0.0-RC11"

  val appCompatVersion = "1.0.2"
  val truthVersion = "0.39"
  val junitVersion = "4.12"
  val robolectricVersion = "3.5.1"
  val jsrVersion = "3.0.2"
  val mockitoVersion = "2.23.4"
  val javaInject = "1"
}