@file:Suppress("UNCHECKED_CAST")

package com.wealthfront

import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.stubbing.OngoingStubbing
import org.mockito.stubbing.Stubber

internal fun <T> whenever(x: T): OngoingStubbing<T> = `when`(x)

internal fun <T> Stubber.whenever(x: T): T = `when`(x)

// Casting to avoid a kotlin-NPE ( See : https://medium.com/@elye.project/befriending-kotlin-and-mockito-1c2e7b0ef791 )
internal fun <T> any(): T {
  Mockito.any<T>()
  return null as T
}

internal fun <T> eq(value: T): T {
  Mockito.eq(value)
  return value
}

internal fun <T> isA(value: Class<T>): T {
  Mockito.isA(value)
  return null as T
}

internal fun <T, R : T> argThat(argumentMatcher: (R) -> Boolean): T {
  Mockito.argThat { arg: R -> argumentMatcher(arg) }
  return null as T
}

internal fun <T> ArgumentCaptor<T?>.captureNotNull(): T {
  this.capture()
  return null as T
}
