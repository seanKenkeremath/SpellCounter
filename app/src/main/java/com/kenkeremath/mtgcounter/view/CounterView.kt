package com.kenkeremath.mtgcounter.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.TextView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.util.MathUtils

abstract class CounterView(
    layoutResId: Int,
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : FrameLayout(
    context,
    attrs,
    defStyleAttr
) {

    companion object {
        /**
         * When in immersive mode we want to be able to show the system controls
         * without registering a click
         */
        private const val MAX_CLICK_DISTANCE_DP = 3
    }

    private var valueText: TextView
    private var value: Int = 0

    private var touchStartX: Float = 0f
    private var touchStartY: Float = 0f

    private var maxTouchDistance: Float

    private var listener: OnValueUpdatedListener? = null

    constructor(layoutResId: Int, context: Context) : this(layoutResId, context, null)
    constructor(layoutResId: Int, context: Context, attrs: AttributeSet?) : this(
        layoutResId,
        context,
        attrs,
        0
    )

    init {
        LayoutInflater.from(context).inflate(layoutResId, this, true)
        valueText = findViewById(R.id.counter)
        maxTouchDistance = resources.displayMetrics.density * MAX_CLICK_DISTANCE_DP
        setValue(0)
    }

    fun setOnValueUpdatedListener(onValueUpdatedListener: OnValueUpdatedListener?) {
        listener = onValueUpdatedListener
    }

    fun setValue(value: Int) {
        valueText.text = "$value"
        this.value = value
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchStartX = it.rawX
                    touchStartY = it.rawY
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    val distance = MathUtils.distance(it.rawX, it.rawY, touchStartX, touchStartY)
                    if (distance > maxTouchDistance) {
                        return false
                    }
                    listener?.onValueIncremented(1)
                    return true
                }
                else -> false
            }
        }
        return false
    }


    interface OnValueUpdatedListener {
        fun onValueSet(value: Int)
        fun onValueIncremented(valueDifference: Int)
    }
}