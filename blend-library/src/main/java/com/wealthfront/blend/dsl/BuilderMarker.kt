package com.wealthfront.blend.dsl

import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.TYPE

@DslMarker
@Target(CLASS, TYPE)
internal annotation class BuilderMarker
