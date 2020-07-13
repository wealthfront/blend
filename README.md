# Blend (Beta)
[![Build Status](https://travis-ci.org/wealthfront/blend.svg?branch=master)](https://travis-ci.org/wealthfront/blend)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.wealthfront/blend/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.wealthfront/blend)

An Android animation framework that gracefully handles interruptions. Currently in beta, as some details of the API may change in future releases.

## Features
* Smoothly blend animations started at different times together
* Easily read and describe animations with a Kotlin DSL
* Use it anywhere where an `Animator` is accepted (e.g. `Transition`s)
* Animate any property on any object, not just `View`s
* Test animations with provided immediate and mock implementations

## Use
```groovy
def blendVersion = "0.2.1"
implementation "com.wealthfront:blend:${blendVersion}"
testImplementation "com.wealthfront:blend-test:${blendVersion}"
```

## Why another animation framework?
In order to make quality animations, we need them to be two things:
* **Correct**: Animations should be predictable and always end in the correct state, regardless of interruptions or competing animations.
* **Smooth**: Animations should be continuous (i.e. never visibly jump in value) and retain velocity (i.e. never visibly jump in speed), regardless of interruptions or competing animations.

Most frameworks in android are missing one or the other, especially since only physics-based animations handle interruption by new animations properly (and even then only if you call `animateToFinalPosition()` instead of `start()`). The problem with the physics-based animation framework is that they don't extend `Animator`, so you can't use them in many places in the android framework (e.g. `Transition`s).

## Interruptions
Why is handling interruptions important? The main reason is that **we, as developers, don't control when the user interacts with the app**. This means that the user could reverse that animation or click something else and move it somewhere else before it's done.

Apple added additive animations by default in iOS 8, and has [a great developer talk about it (starting around 17:50)](https://developer.apple.com/videos/play/wwdc2014/236/). If you don't want to spend the time to watch it, here's a gif showing the difference:

![Blend example](https://media.giphy.com/media/W0QiIZkNqHQSSdMFpZ/giphy.gif)

## I'm sold; how do I use it?
First, see above for download instructions. Then, create a `Blend` instance (called `blend` in the following examples) to get started.

### Simple animations
```kotlin
blend {
  target(view).animations {
    fadeIn()
    translationX(200f)
  }
}.start()
```
Blend includes a wide library of standard properties to animate in `AdditiveViewProperties`, including elevation and margin/padding.

### Animations with multiple targets
If you want all targets to do the same thing:
```kotlin
blend {
  target(view1, view2).animations {
    fadeIn()
    translationX(200f)
  }
}.start()
```

Or if you want them to do different things at the same time:
```kotlin
blend {
  target(view1).animations {
    fadeIn()
  }
  target(view2).animations {
    translationX(200f)
  }
}.start()
```

### Animations with custom duration and easing
```kotlin
blend {
  accelerate()
  duration(200, MILLISECONDS)
  target(view1).animations {
    fadeIn()
  }
}.start()
```
Blend includes some convenience methods to cover most cases with the Material-Design-approved easing functions.
* `defaultEase()` (default) = FastOutSlowIn
* `accelerate()` = FastOutLinearIn
* `decelerate()` = LinearOutSlowIn
* `overshoot()` = Overshoot

### Chaining animations in time
Animations chained in time will blend in the same way as normal animations. That is, whichever set of animations `.start()`ed last will define the end state (when all running animations have settled). This means that during the animation, some properties may go outside of their expected bounds, but will always end up in a predictable state.

```kotlin
blend {
  target(view1).animations {
    fadeIn()
  }
}.then {
  target(view2).animations {
    translationX(200f)
  }
}.start()
```
```kotlin
blend {
  target(view1).animations {
    fadeIn()
  }
}.with {
  startDelay(200, MILLISECONDS)
  target(view2).animations {
    translationX(200f)
  }
}.start()
```

### Use as an Animator
Calling `.prepare()` instead of `.start()` will return a standard Android `Animator` (or rather, a subclass of it called `BlendableAnimator`) that includes the entire animation specified. Use this to interface with the android framework.
```kotlin
val myAnimator = blend {
  target(view).animations {
    fadeIn()
    translationX(200f)
  }
}.prepare()
```

### Animations on custom objects
To animate properties not included in the standard DSL (see `AdditiveViewProperties`) or on a non-view object, simply create a new `AdditiveProperty` and either pass it into `AnimationBuilder.genericProperty` or create an extension function on `AnimationBuilder`.

Using `makeExternalAdditiveViewProperty` is recommended for non-view objects that reside in the UI.

## Testing

### `MockBlend`
For unit-testing your views, use `MockBlend`. It allows for assertions on a given subject like:

```kotlin
mockBlend.assertThat(view).isFadedIn()
mockBlend.assertThat(view).isCollapsed()
mockBlend.assertThat(view).hasPropertySetTo(MY_CUSTOM_PROPERTY, 100f)
mockBlend.assertThat(otherView).isNeverAnimated()
```

### `ImmediateBlend`
For integration testing, or for unit testing where you want the animations to be applied like a `set` call rather than queued for verification, use `ImmediateBlend`. Pass an instance in and all animations and listeners will run immediately, without ever starting a ValueAnimator.

## Configuration

### Providing default values (duration, easing/interpolation, etc.)
To change the default values of animations, simply subclass `Blend` and override `createBlendableAnimator` with your default values (duration, ease/interpolator, etc.) set.

## License

```
Copyright 2019 Wealthfront, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
