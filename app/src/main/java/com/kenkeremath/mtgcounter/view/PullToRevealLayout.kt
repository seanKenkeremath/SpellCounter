package com.kenkeremath.mtgcounter.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.math.MathUtils.clamp
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.util.LogUtils
import kotlin.math.abs

class PullToRevealLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var pullEnabled = true

    private var revealing = false
    private var revealState = RevealState.HIDDEN
    private val revealed: Boolean
        get() = revealState != RevealState.HIDDEN

    enum class RevealState {
        HIDDEN,
        REVEALED_DOWN,
        REVEALED_UP
    }

    private var startTime = 0L
    private var startY = 0f
    private var startTranslationY = 0f

    private val triggerDistance =
        resources.getDimensionPixelSize(R.dimen.pull_to_reveal_trigger_distance)
    private val flingMaxTime = 800L
    private val animationDuration = 300L
    private val flingMinDistance =
        resources.getDimensionPixelSize(R.dimen.pull_to_reveal_fling_min_distance)
    private val translationZMax = resources.getDimension(R.dimen.pull_to_reveal_translation_z_max)

    private var animation: Animator? = null

    var listener: PullToRevealListener? = null

    fun setPullEnabled(enabled: Boolean) {
        pullEnabled = enabled
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (!pullEnabled || !isEnabled) {
            return false
        }
        revealChild?.let { revealChild ->
            ev?.let {
                if (it.action == MotionEvent.ACTION_DOWN) {
                    startTime = System.currentTimeMillis()
                    startY = it.y
                    startTranslationY = revealChild.translationY
                    if (revealing) {
                        cancelAnimation()
                    }
                    LogUtils.d(tag = LogUtils.TAG_PULL_TO_REVEAL, message = "Down at: ${it.y}")
                } else {
                    val rawTranslation = it.y - startY
                    val translation = clamp(
                        rawTranslation,
                        -height.toFloat() - startTranslationY,
                        height.toFloat() - startTranslationY
                    )
                    LogUtils.d(
                        tag = LogUtils.TAG_PULL_TO_REVEAL,
                        message = "Movement: y: ${it.y}, startTranslation: $startTranslationY delta: $rawTranslation, clamped Delta: $translation height: $height"
                    )
                    if (it.action == MotionEvent.ACTION_MOVE) {
                        if (revealed) {
                            if (revealing || abs(rawTranslation) > triggerDistance) {
                                revealing = true
                                listener?.onDragging()
                                LogUtils.d(
                                    tag = LogUtils.TAG_PULL_TO_REVEAL,
                                    message = "Hide Translation Triggered: $translation"
                                )
                                revealChild.translationY = startTranslationY + translation
                                revealChild.translationZ = (abs(revealChild.translationY) / revealChild.height) * translationZMax
                            }
                        } else {
                            if (revealing || abs(rawTranslation) > triggerDistance) {
                                revealing = true
                                listener?.onDragging()
                                LogUtils.d(
                                    tag = LogUtils.TAG_PULL_TO_REVEAL,
                                    message = "Reveal Translation Triggered: $translation"
                                )
                                revealChild.translationY = startTranslationY + translation
                                revealChild.translationZ = (abs(revealChild.translationY) / revealChild.height) * translationZMax
                            }
                        }
                    } else if (it.action == MotionEvent.ACTION_UP) {
                        LogUtils.d(
                            tag = LogUtils.TAG_PULL_TO_REVEAL,
                            message = "Resetting PTR (ACTION_UP)"
                        )
                        val fling =
                            abs(rawTranslation) > flingMinDistance
                                    && System.currentTimeMillis() - startTime < flingMaxTime

                        if (fling) {
                            if (!revealed) {
                                LogUtils.d(
                                    tag = LogUtils.TAG_PULL_TO_REVEAL,
                                    message = "Reveal fling detected"
                                )
                                animateReveal(revealUp = rawTranslation < 0f)
                            } else {
                                LogUtils.d(
                                    tag = LogUtils.TAG_PULL_TO_REVEAL,
                                    message = "Hide fling detected"
                                )
                                animateHide()
                            }
                        } else {
                            animateBasedOnTranslation()
                        }
                    } else if (it.action == MotionEvent.ACTION_CANCEL) {
                        LogUtils.d(
                            tag = LogUtils.TAG_PULL_TO_REVEAL,
                            message = "Resetting PTR (ACTION_CANCEL)"
                        )
                        animateBasedOnTranslation()
                    }
                }
            }
        }
        return false
    }

    private val revealChild: View?
        get() {
            return if (childCount > 1) {
                getChildAt(1)
            } else {
                null
            }
        }

    private fun animateBasedOnTranslation() {
        revealChild?.let {
            if (revealing) {
                if (!revealed) {
                    if (it.translationY > height / 2) {
                        animateReveal(revealUp = false)
                    } else if (it.translationY < -height / 2) {
                        animateReveal(revealUp = true)
                    } else {
                        animateHide()
                    }
                } else if (revealState == RevealState.REVEALED_DOWN) {
                    if (it.translationY < height * 5 / 6) {
                        animateHide()
                    } else {
                        animateReveal(revealUp = false)
                    }
                } else if (revealState == RevealState.REVEALED_UP) {
                    if (it.translationY > -height * 5 / 6) {
                        animateHide()
                    } else {
                        animateReveal(revealUp = true)
                    }
                }
            }
        }
    }

    //Must be called after measure
    fun reveal(animate: Boolean) {
        if (animate) {
            animateReveal(revealUp = false)
        } else {
            cancelAnimation()
            revealChild?.translationY = height.toFloat()
            revealChild?.translationZ = translationZMax
            revealState = RevealState.REVEALED_UP
            revealing = false
            listener?.onReveal()
        }
    }

    fun hide(animate: Boolean) {
        if (animate) {
            animateHide()
        } else {
            cancelAnimation()
            revealChild?.translationY = 0f
            revealChild?.translationZ = 0f
            revealState = RevealState.HIDDEN
            revealing = false
            listener?.onHide()
        }
    }

    private fun animateHide() {
        revealChild?.let {
            cancelAnimation()
            LogUtils.d(
                tag = LogUtils.TAG_PULL_TO_REVEAL,
                message = "Hiding"
            )
            val translationAnimation =
                ObjectAnimator.ofFloat(it, "translationY", it.translationY, 0f)
            val elevationAnimation =
                ObjectAnimator.ofFloat(it, View.TRANSLATION_Z, it.translationZ, 0f)
            val set = AnimatorSet()
            set.playTogether(translationAnimation, elevationAnimation)
            animation = set
            animation?.duration = animationDuration
            animation?.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    LogUtils.d(
                        tag = LogUtils.TAG_PULL_TO_REVEAL,
                        message = "Animation Ended"
                    )
                    revealing = false
                    revealState = RevealState.HIDDEN
                    listener?.onHide()
                }
            })
            animation?.start()
        }
    }

    private fun animateReveal(revealUp: Boolean) {
        revealChild?.let {
            cancelAnimation()
            LogUtils.d(
                tag = LogUtils.TAG_PULL_TO_REVEAL,
                message = "Revealing"
            )
            val translationAnimation =
                ObjectAnimator.ofFloat(
                    it, "translationY", it.translationY,
                    height.toFloat() * if (revealUp) -1 else 1
                )
            val elevationAnimation =
                ObjectAnimator.ofFloat(it, View.TRANSLATION_Z, it.translationZ, translationZMax)
            val set = AnimatorSet()
            set.playTogether(translationAnimation, elevationAnimation)
            animation = set
            animation?.duration = animationDuration
            animation?.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    LogUtils.d(
                        tag = LogUtils.TAG_PULL_TO_REVEAL,
                        message = "Animation Ended"
                    )
                    revealing = false
                    revealState =
                        if (revealUp) RevealState.REVEALED_UP else RevealState.REVEALED_DOWN
                    listener?.onReveal()
                }
            })
            animation?.start()
        }
    }

    private fun cancelAnimation() {
        animation?.removeAllListeners()
        animation?.cancel()
    }

    interface PullToRevealListener {
        fun onReveal()
        fun onHide()
        fun onDragging()
    }
}