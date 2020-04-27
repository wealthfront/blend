package com.wealthfront.blend.sample

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.inflate
import android.widget.FrameLayout
import androidx.annotation.VisibleForTesting
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.wealthfront.blend.ALPHA_FULL
import com.wealthfront.blend.sample.R
import com.wealthfront.blend.Blend
import com.wealthfront.blend.properties.AdditiveProperty
import com.wealthfront.blend.properties.AdditiveViewProperties
import kotlin.math.absoluteValue

class AnimationDemoView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

  @VisibleForTesting var topY = context.resources.getDimension(R.dimen.normalPadding)
  private var _bottomY: Float? = null
  @VisibleForTesting var bottomY: Float
    get() = _bottomY ?: height - demoCircle.height - topY
    set(newY) {
      _bottomY = newY
    }

  var demoCircle: View by bindView(R.id.demoCircle)
  var demoCircle2: View by bindView(R.id.demoCircle2)

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

  fun animateCircleWithBlend(view: View) = blend {
    duration(2000)
    target(view).animations {
      if (view.y isCloseEnoughTo bottomY) {
        y(topY)
      } else {
        y(bottomY)
      }
    }
  }.start()
}

private const val FLOAT_TOLERANCE = 1f
private infix fun Float?.isCloseEnoughTo(other: Float) = this != null && (this - other).absoluteValue < FLOAT_TOLERANCE
