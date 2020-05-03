package com.wealthfront.blend.dsl

import com.wealthfront.blend.animator.BlendableAnimator
import com.wealthfront.blend.properties.AdditiveProperty

@BuilderMarker
open class AnimationBuilder<out Subject : Any>(
  val currentSubjects: List<Subject>,
  override val currentAnimator: BlendableAnimator
) : ListenerBuilder {

  /**
   * A utility method to help subclasses easily add animations without needing to explicitly construct
   * [com.wealthfront.blend.animator.SinglePropertyAnimation]s.
   *
   * @param subject The subject of the animation
   * @param property The property to animate
   * @param targetValue The value to animate [property] to
   */
  fun <Subject : Any> addAnimation(
    targetValue: Float,
    property: AdditiveProperty<Subject>,
    subject: Subject
  ) {
    currentAnimator.addAnimation(subject, property, targetValue)
  }
}

/**
 * Add an animation of [property] for all [AnimationBuilder.currentSubjects].
 *
 * @param property The property to animate
 * @param targetValue The value to animate [property] to
 */
fun <Subject : Any> AnimationBuilder<Subject>.customProperty(targetValue: Float, property: AdditiveProperty<Subject>) =
  currentSubjects.forEach { subject -> currentAnimator.addAnimation(subject, property, targetValue) }
