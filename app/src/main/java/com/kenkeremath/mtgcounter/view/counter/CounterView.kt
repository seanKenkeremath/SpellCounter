package com.kenkeremath.mtgcounter.view.counter

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.util.CounterUtils
import com.kenkeremath.mtgcounter.view.HoldableButton

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

    private var amountText: TextView
    private var amount: Int = 0
    private val incrementer: HoldableButton
    private val decrementer: HoldableButton

    private var listener: OnAmountUpdatedListener? = null

    companion object {
        private const val LABEL_HEIGHT_TO_WIDTH_RATIO = 1.3f
    }

    constructor(layoutResId: Int, context: Context) : this(layoutResId, context, null)
    constructor(layoutResId: Int, context: Context, attrs: AttributeSet?) : this(
        layoutResId,
        context,
        attrs,
        0
    )

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        /**
         * Auto Size text view can clip at large text sizes when the ratio of width
         * to height is low. Dynamically set a ratio to prevent this from occurring.
         *
         * NOTE: This is not being done in the ConstraintLayout because other constraints
         * are necessary for the general layout that would not be compatible with an additional
         * ratio constraint
         */
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            amountText,
            resources.getDimensionPixelSize(R.dimen.counter_label_min_text_size),
            (w * LABEL_HEIGHT_TO_WIDTH_RATIO).toInt(),
            1,
            TypedValue.COMPLEX_UNIT_PX,
        )
    }

    init {
        //Leaking instance information is safe in this init because we are not referencing any subclass info
        @Suppress("LeakingThis")
        LayoutInflater.from(context).inflate(layoutResId, this, true)
        amountText = findViewById(R.id.amount)
        incrementer = findViewById(R.id.increment_button)
        decrementer = findViewById(R.id.decrement_button)
        setAmount(0)

        incrementer.setListener(object:
            HoldableButton.HoldableButtonListener {
            override fun onSingleClick() {
                listener?.onAmountIncremented(1)
            }
            override fun onHoldContinued(increments: Int) {
                listener?.onAmountIncremented(CounterUtils.getAmountChangeForHoldIteration(increments))
            }
        })

        decrementer.setListener(object:
            HoldableButton.HoldableButtonListener {
            override fun onSingleClick() {
                listener?.onAmountIncremented(-1)
            }
            override fun onHoldContinued(increments: Int) {
                listener?.onAmountIncremented(CounterUtils.getAmountChangeForHoldIteration(increments) * -1)
            }
        })
    }

    fun setOnAmountUpdatedListener(onAmountUpdatedListener: OnAmountUpdatedListener?) {
        listener = onAmountUpdatedListener
    }

    fun setAmount(amount: Int) {
        amountText.text = "$amount"
        this.amount = amount
    }

    interface OnAmountUpdatedListener {
        fun onAmountSet(amount: Int)
        fun onAmountIncremented(amountDifference: Int)
    }
}