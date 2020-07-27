package com.wealthfront.blend.sample

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat.getColor
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.card.MaterialCardView
import com.wealthfront.blend.Blend
import com.wealthfront.blend.dsl.customProperty
import com.wealthfront.blend.dsl.y
import com.wealthfront.blend.properties.blendColor
import com.wealthfront.blend.properties.makeAdditiveViewProperty
import kotlin.math.absoluteValue

class AnimationDemoView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

  @VisibleForTesting var topY = context.resources.getDimension(R.dimen.normalPadding)
  private var _bottomY: Float? = null
  @VisibleForTesting var bottomY: Float
    get() = _bottomY ?: label.y - demoCircle.height - topY
    set(newY) {
      _bottomY = newY
    }

  var demoCircle: MaterialCardView by bindView(R.id.demoCircle)
  var demoCircle2: MaterialCardView by bindView(R.id.demoCircle2)
  var label: TextView by bindView(R.id.label)

  @VisibleForTesting internal var blend: Blend

  init {
    inflate(context, R.layout.animation_demo, this)
    blend = Blend()
    setOnClickListener {
      animateCircles()
    }
    systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
  }

  fun animateCircles() {
    animateCircleWithBlend(demoCircle)
    animateCircleWithDiscontinuousVelocity(demoCircle2)
  }

  fun animateCircleWithDiscontinuousVelocity(view: View) {
    view.animate()
      .setInterpolator(FastOutSlowInInterpolator())
      .setDuration(2000)
      .y(if (view.y isCloseEnoughTo bottomY) {
        topY
      } else {
        bottomY
      })
      .start()
  }

  fun animateCircleWithDiscontinuousPosition(view: View) {
    view.y = if (view.y isCloseEnoughTo bottomY) {
      bottomY
    } else {
      topY
    }
    animateCircleWithDiscontinuousVelocity(view)
  }

  fun animateCircleWithBlend(view: MaterialCardView) {
    if (view.y isCloseEnoughTo bottomY) {
      blend.pulse(demoCircle)
    } else {
      blend.stopPulsing()
    }
    blend {
      duration(2000)
      target(view).animations {
        if (view.y isCloseEnoughTo bottomY) {
          y(topY)
          customProperty(Color.GREEN.toFloat(), strokeProperty)
        } else {
          y(bottomY)
          customProperty(getColor(context, R.color.colorPrimary).toFloat(), strokeProperty)
        }
      }
    }.start()
  }

  val strokeProperty = makeAdditiveViewProperty<MaterialCardView>(
    R.id.demoCircle,
    get = { subject -> subject.strokeColorStateList!!.defaultColor.toFloat() },
    set = { subject, value -> subject.strokeColor = value.toInt() },
    interpolateAction = { startValue, endValue, timeFraction, _ ->
      blendColor(startValue, endValue, timeFraction)
    }
  )
}

private const val FLOAT_TOLERANCE = 10f
private infix fun Float?.isCloseEnoughTo(other: Float) = this != null && (this - other).absoluteValue < FLOAT_TOLERANCE
