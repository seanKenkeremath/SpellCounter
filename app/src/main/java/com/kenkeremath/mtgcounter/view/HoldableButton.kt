package com.kenkeremath.mtgcounter.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.util.LogUtils
import com.kenkeremath.mtgcounter.util.MathUtils

class HoldableButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        /**
         * When in immersive mode we want to be able to show the system controls
         * without registering a click
         */
        private const val MAX_CLICK_DISTANCE_DP = 16

        /**
         * As the button is held down the intervals between events will speed up between max and min
         */
        private const val MAX_HOLD_INTERVAL = 600L
        private const val MIN_HOLD_INTERVAL = 10L
    }

    private var maxTouchDistance: Float = resources.displayMetrics.density * MAX_CLICK_DISTANCE_DP

    private var touchStartX: Float = 0f
    private var touchStartY: Float = 0f

    private var holdStartTime: Long = 0
    private val holdIntervals = SparseArray<Long>()
    private var holdIncrements = 0
    private val holdGestureHandler = Handler(Looper.getMainLooper())
    private val holdGestureRunnable = object : Runnable {
        override fun run() {
            holdIncrements++
            LogUtils.d("Sending hold callback: Iteration $holdIncrements", LogUtils.TAG_INCREMENTER)
            listener?.onHoldContinued(holdIncrements)
            var interval = MAX_HOLD_INTERVAL
            for (i in 0 until holdIntervals.size()) {
                if (i == holdIntervals.size() - 1) {
                    interval = holdIntervals[holdIntervals.keyAt(i)]
                    break
                } else if (holdIncrements >= holdIntervals.keyAt(i) && holdIncrements < holdIntervals.keyAt(
                        i + 1
                    )
                ) {
                    val prevIntervalTick = holdIntervals.keyAt(i)
                    val nextIntervalTick = holdIntervals.keyAt(i + 1)
                    val totalTickDelta = nextIntervalTick - prevIntervalTick
                    val totalIntervalDelta =
                        holdIntervals[nextIntervalTick] - holdIntervals[prevIntervalTick]
                    val currTickDelta = holdIncrements - prevIntervalTick
                    interval =
                        holdIntervals[prevIntervalTick] + (currTickDelta / totalTickDelta.toFloat() * totalIntervalDelta).toInt()
                    break
                }
            }
            holdGestureHandler.postDelayed(this, interval)
        }
    }

    private var listener: HoldableButtonListener? = null

    init {
        //Set up timing of increment callbacks while holding down
        holdIntervals.append(0, MAX_HOLD_INTERVAL)
        holdIntervals.append(6, 400L)
        holdIntervals.append(15, 200L)
        holdIntervals.append(30, 150L)
        holdIntervals.append(100, 30L)
        holdIntervals.append(500, MIN_HOLD_INTERVAL)
        background = ContextCompat.getDrawable(context, R.drawable.incrementer_bg)

        foreground = RippleDrawable(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    R.color.ripple_color
                )
            ),
            null,
            null
        )
    }

    fun setListener(holdableButtonListener: HoldableButtonListener) {
        listener = holdableButtonListener
    }

    private fun resetHold() {
        holdIncrements = 0
        holdStartTime = 0
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        holdGestureHandler.removeCallbacks(holdGestureRunnable)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isEnabled) {
            return true
        }
        event?.let {
            background?.setHotspot(it.x, it.y)
            foreground?.setHotspot(it.x, it.y)
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    LogUtils.d("Hold gesture started", LogUtils.TAG_INCREMENTER)
                    touchStartX = it.rawX
                    touchStartY = it.rawY
                    isPressed = true
                    resetHold()
                    holdStartTime = System.currentTimeMillis()
                    holdGestureHandler.postDelayed(holdGestureRunnable, MAX_HOLD_INTERVAL)
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    LogUtils.d("Hold touch moved", LogUtils.TAG_INCREMENTER)
                    val distance = MathUtils.distance(it.rawX, it.rawY, touchStartX, touchStartY)
                    if (distance > maxTouchDistance) {
                        LogUtils.d(
                            "Hold touch exceeded max distance. Canceling gesture.",
                            LogUtils.TAG_INCREMENTER
                        )
                        isPressed = false
                        holdGestureHandler.removeCallbacks(holdGestureRunnable)
                        return false
                    }
                    return true
                }
                MotionEvent.ACTION_CANCEL -> {
                    LogUtils.d("Canceling hold gesture", LogUtils.TAG_INCREMENTER)
                    isPressed = false
                    holdGestureHandler.removeCallbacks(holdGestureRunnable)
                }
                MotionEvent.ACTION_UP -> {
                    LogUtils.d("Hold gesture complete", LogUtils.TAG_INCREMENTER)
                    isPressed = false
                    holdGestureHandler.removeCallbacks(holdGestureRunnable)
                    val distance = MathUtils.distance(it.rawX, it.rawY, touchStartX, touchStartY)
                    return if (distance < maxTouchDistance && System.currentTimeMillis() - holdStartTime < MAX_HOLD_INTERVAL) {
                        LogUtils.d("Hold gesture registered as click", LogUtils.TAG_INCREMENTER)
                        performClick()
                        true
                    } else {
                        LogUtils.d(
                            "Hold gesture moved too much or took too long to be registered as a click",
                            LogUtils.TAG_INCREMENTER
                        )
                        false
                    }
                }
                else -> false
            }
        }
        return false
    }

    interface HoldableButtonListener {
        fun onSingleClick()
        fun onHoldContinued(increments: Int)
    }

    override fun performClick(): Boolean {
        super.performClick()
        listener?.onSingleClick()
        return true
    }
}