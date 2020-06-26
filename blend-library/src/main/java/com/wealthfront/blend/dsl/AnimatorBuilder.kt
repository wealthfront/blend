package com.wealthfront.blend.dsl

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.wealthfront.blend.animator.BlendableAnimator
import com.wealthfront.blend.builder.AnimatorSetWrapper
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.MILLISECONDS

/**
 * The class responsible for setting animator-level attribuites.
 *
 * These include setting easing and duration ([TimingBuilder]) and adding listeners ([ListenerBuilder]). We include
 * chaining ([SetBuilder]) so we can create arbitrary animation graphs, and so composition functions like [stagger]
 * can work in this context.
 *
 * We also have the various [target] functions to specify animation subjects and properties (via [AnimationBuilder]
 * and its subclasses).
 */
class AnimatorBuilder(
  override val currentAnimator: BlendableAnimator,
  private val animatorSet: AnimatorSetWrapper,
  private val createNewBlendableAnimator: () -> BlendableAnimator
) : TimingBuilder, ListenerBuilder, SetBuilder<AnimatorBuilder> {

  /**
   * Target arbitrary objects for animation.
   */
  fun <T : Any> target(vararg subjects: T) = target(subjects.toList())

  /**
   * Target arbitrary objects for animation.
   */
  @JvmName("targetSubjects")
  fun <T : Any> target(subjects: List<T>) =
    Targetable(AnimationBuilder(subjects, currentAnimator))

  /**
   * Stagger animations on the given subjects.
   *
   * Creates an animator for each subject, offset by [timeBetweenTargets] * [timeUnit], and applies [action] to them.
   */
  fun <T : Any> stagger(
    vararg subjects: T,
    timeBetweenTargets: Long,
    timeUnit: TimeUnit = MILLISECONDS,
    action: AnimatorBuilder.(T) -> Unit
  ) {
    stagger(subjects.toList(), timeBetweenTargets, timeUnit, action)
  }

  /**
   * Stagger animations on the given subjects.
   *
   * Creates an animator for each subject, offset by [timeBetweenTargets] * [timeUnit], and applies [action] to them.
   */
  @Suppress("NestedBlockDepth")
  fun <Subject : Any> stagger(
    subjects: List<Subject>,
    timeBetweenTargets: Long,
    timeUnit: TimeUnit = MILLISECONDS,
    action: AnimatorBuilder.(Subject) -> Unit
  ) {
    var currentBuilder = this
    subjects.forEach { currentSubject ->
      if (currentBuilder.currentAnimator.allAnimations.isNotEmpty()) {
        currentBuilder = currentBuilder.with {
          this.action(currentSubject)
          this.startDelay(currentBuilder.currentAnimator.startDelay + timeUnit.toMillis(timeBetweenTargets), MILLISECONDS)
        }
      } else {
        currentBuilder.action(currentSubject)
      }
    }
  }

  override fun then(action: AnimatorBuilder.() -> Unit): AnimatorBuilder {
    val newAnimator = createNewBlendableAnimator()
    animatorSet.addSequential(currentAnimator, newAnimator)
    return AnimatorBuilder(newAnimator, animatorSet, createNewBlendableAnimator).apply(action)
  }

  override fun with(action: AnimatorBuilder.() -> Unit): AnimatorBuilder {
    val newAnimator = createNewBlendableAnimator()
    newAnimator.startDelay = currentAnimator.startDelay
    animatorSet.addSimultaneous(currentAnimator, newAnimator)
    return AnimatorBuilder(newAnimator, animatorSet, createNewBlendableAnimator).apply(action)
  }
}

/**
 * A utility class that allows us to make targeting work.
 *
 * In an ideal world, this wouldn't be necessary and we could pass the action directly into the various
 * [AnimatorBuilder.target] methods, but that breaks Kotlin's overloaded method resolution.
 */
class Targetable<Builder : AnimationBuilder<*>>(val builder: Builder) {

  /**
   * Specify animations to run on the current targets.
   *
   * The targets are the [builder]'s [AnimationBuilder.currentSubjects].
   *
   * See the various [AnimationBuilder]s for allowed animations on different subjects.
   */
  fun animations(action: Builder.() -> Unit) {
    builder.action()
  }
}
