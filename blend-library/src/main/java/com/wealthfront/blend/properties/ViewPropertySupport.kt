package com.wealthfront.blend.properties

import android.graphics.Color
import android.util.Property
import android.view.View
import androidx.annotation.IdRes
import androidx.core.view.ViewCompat
import kotlin.math.roundToInt

/**
 * Make an [AdditiveProperty] for a [Subject] that stores its [AnimationData] on [view].
 *
 * The [AnimationData] is keyed off of the [id] and the subject for a given animation, so it's possible to start
 * multiple animations of this property and store them on the same [view].
 *
 * @param id The id resource that is unique to this property
 * @param view The view on which to store the [AnimationData]
 * @param get The getter function
 * @param set The setter function
 * @param interpolateAction The action to interpolate values (if not linear, e.g. for color properties)
 * @param setUpAction The action to run when any animation for this property starts, if needed
 */
fun <Subject : Any> makeExternalAdditiveViewProperty(
  @IdRes id: Int,
  view: View,
  get: (subject: Subject) -> Float,
  set: (subject: Subject, value: Float) -> Unit,
  interpolateAction: ((startValue: Float, endValue: Float, timeFraction: Float, subject: Subject) -> Float)? = null,
  setUpAction: ((subject: Subject) -> Unit)? = null
): AdditiveProperty<Subject> {
  return object : AdditiveProperty<Subject> {
    override val id: Int = id

    override fun setValue(subject: Subject, value: Float) = set(subject, value)

    override fun getCurrentValue(subject: Subject): Float = get(subject)

    override fun getAnimationData(subject: Subject): AnimationData = view.getAnimationDataMap(id).getOrCreate(subject)

    override fun interpolate(startValue: Float, endValue: Float, timeFraction: Float, subject: Subject): Float =
        if (interpolateAction == null) {
          super.interpolate(startValue, endValue, timeFraction, subject)
        } else {
          interpolateAction(startValue, endValue, timeFraction, subject)
        }

    override fun setUpOnAnimationQueued(subject: Subject) {
      setUpAction?.invoke(subject)
    }
  }
}

private fun View.getAnimationDataMap(@IdRes id: Int): MutableMap<Any, AnimationData> {
  @Suppress("UNCHECKED_CAST")
  var animationDataMap = this.getTag(id) as MutableMap<Any, AnimationData>?
  if (animationDataMap == null) {
    animationDataMap = mutableMapOf()
    this.setTag(id, animationDataMap)
  }
  return animationDataMap
}

private fun MutableMap<Any, AnimationData>.getOrCreate(key: Any): AnimationData {
  var animationData = this[key]
  if (animationData == null) {
    animationData = AnimationData()
    this[key] = animationData
  }
  return animationData
}

/**
 * Make an [AdditiveProperty] for a [Subject].
 *
 * @param id The id resource that is unique to this property
 * @param get The getter function
 * @param set The setter function
 * @param interpolateAction The action to interpolate values (if not linear, e.g. for color properties)
 * @param setUpAction The action to run when any animation for this property starts, if needed
 */
internal fun <Subject : View> makeAdditiveViewProperty(
  @IdRes id: Int,
  get: (subject: Subject) -> Float,
  set: (subject: Subject, value: Float) -> Unit,
  interpolateAction: ((startValue: Float, endValue: Float, timeFraction: Float, subject: Subject) -> Float)? = null,
  setUpAction: ((subject: Subject) -> Unit)? = null
): AdditiveProperty<Subject> {
  return object : AdditiveProperty<Subject> {
    override val id: Int = id

    override fun setValue(subject: Subject, value: Float) = set(subject, value)

    override fun getCurrentValue(subject: Subject): Float = get(subject)

    override fun getAnimationData(subject: Subject): AnimationData = subject.getAnimationData(id)

    override fun interpolate(startValue: Float, endValue: Float, timeFraction: Float, subject: Subject): Float =
      if (interpolateAction == null) {
        super.interpolate(startValue, endValue, timeFraction, subject)
      } else {
        interpolateAction(startValue, endValue, timeFraction, subject)
      }

    override fun setUpOnAnimationQueued(subject: Subject) {
      setUpAction?.invoke(subject)
    }
  }
}

/**
 * Same as [makeAdditiveViewProperty] but for animations where the value is an integer.
 *
 * @param id The id resource that is unique to this property
 * @param get The getter function
 * @param set The setter function
 * @param setUpAction The action to run when any animation for this property starts, if needed
 */
internal fun <Subject : View> makeAdditiveIntViewProperty(
  @IdRes id: Int,
  get: (subject: Subject) -> Float,
  set: (subject: Subject, value: Float) -> Unit,
  setUpAction: ((subject: Subject) -> Unit)? = null
) = makeAdditiveViewProperty(
    id = id,
    get = get,
    set = set,
    interpolateAction = { startValue, endValue, timeFraction, _ ->
      linearlyInterpolate(startValue.toInt(), endValue.toInt(), timeFraction)
          .toFloat()
    },
    setUpAction = setUpAction
)

/**
 * Make an [AdditiveProperty] for [View] which wraps [property].
 *
 * @param property The view [Property] to wrap
 * @param id The id resource that is unique to this property
 * @param setUpAction The action to run when any animation for this property starts, if needed
 * @param shouldRequestLayout whether this property should request a layout of the subject on every set
 */
internal fun wrapViewProperty(
  property: Property<View, Float>,
  @IdRes id: Int,
  setUpAction: ((subject: View) -> Unit)? = null,
  shouldRequestLayout: Boolean = false
): AdditiveProperty<View> {
  return object : AdditiveProperty<View> {
    override val id: Int = id

    override fun setValue(subject: View, value: Float) {
      property.set(subject, value)
      if (shouldRequestLayout) {
        subject.requestLayoutIfNotAlreadyInLayout()
      }
    }

    override fun getCurrentValue(subject: View): Float = property.get(subject)

    override fun getAnimationData(subject: View): AnimationData = subject.getAnimationData(id)

    override fun setUpOnAnimationQueued(subject: View) {
      setUpAction?.invoke(subject)
    }
  }
}

internal fun View.requestLayoutIfNotAlreadyInLayout() {
  if (!ViewCompat.isInLayout(this)) {
    requestLayout()
  }
}

internal fun View.getAnimationData(@IdRes id: Int): AnimationData {
  @Suppress("UNCHECKED_CAST")
  var animationData = this.getTag(id) as AnimationData?
  if (animationData == null) {
    animationData = AnimationData()
    this.setTag(id, animationData)
  }
  return animationData
}

fun blendColor(startValue: Float, endValue: Float, timeFraction: Float): Float {
  val startInt = startValue.roundToInt()
  val startA = Color.alpha(startInt)
  val startR = Color.red(startInt)
  val startG = Color.green(startInt)
  val startB = Color.blue(startInt)

  val endInt = endValue.roundToInt()
  val endA = Color.alpha(endInt)
  val endR = Color.red(endInt)
  val endG = Color.green(endInt)
  val endB = Color.blue(endInt)

  val midA = linearlyInterpolate(startA, endA, timeFraction)
  val midR = linearlyInterpolate(startR, endR, timeFraction)
  val midG = linearlyInterpolate(startG, endG, timeFraction)
  val midB = linearlyInterpolate(startB, endB, timeFraction)

  return ((midA shl 24) or (midR shl 16) or (midG shl 8) or midB).toFloat()
}