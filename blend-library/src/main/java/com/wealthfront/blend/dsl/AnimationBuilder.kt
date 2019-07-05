package com.wealthfront.blend.dsl

import com.wealthfront.blend.animator.BlendableAnimator
import com.wealthfront.blend.properties.AdditiveProperty

@BuilderMarker
interface AnimationBuilder<out Subject : Any> : ListenerBuilder {
  val currentSubjects: List<Subject>

  /**
   * A utility method to help subclasses easily add animations without needing to explicitly construct
   * [com.wealthfront.blend.animator.SinglePropertyAnimation]s.
   *
   * @param subject The subject of the animation
   * @param property The property to animate
   * @param targetValue The value to animate [property] to
   */
  fun <Subject : Any> addAnimation(
    subject: Subject,
    property: AdditiveProperty<Subject>,
    targetValue: Float
  ) {
    currentAnimator.addAnimation(subject, property, targetValue)
  }

  /**
   * Add an animation of [property] for all [currentSubjects].
   *
   * @param property The property to animate
   * @param targetValue The value to animate [property] to
   */
  fun genericProperty(targetValue: Float, property: AdditiveProperty<Subject>) =
      currentSubjects.forEach { subject -> addAnimation(subject, property, targetValue) }
}

/**
 * An [AnimationBuilder] for arbitrary object types.
 */
class GenericAnimationBuilder<out Subject : Any>(
  override val currentSubjects: List<Subject>,
  override val currentAnimator: BlendableAnimator
) : AnimationBuilder<Subject>
