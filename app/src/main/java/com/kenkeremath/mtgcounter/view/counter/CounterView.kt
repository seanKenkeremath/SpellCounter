package com.kenkeremath.mtgcounter.view.counter

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.util.CounterUtils
import com.kenkeremath.mtgcounter.view.CounterIncrementerButton

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
    private val incrementer: CounterIncrementerButton
    private val decrementer: CounterIncrementerButton

    private var listener: OnAmountUpdatedListener? = null

    constructor(layoutResId: Int, context: Context) : this(layoutResId, context, null)
    constructor(layoutResId: Int, context: Context, attrs: AttributeSet?) : this(
        layoutResId,
        context,
        attrs,
        0
    )

    init {
        //Leaking instance information is safe in this init because we are not referencing any subclass info
        @Suppress("LeakingThis")
        LayoutInflater.from(context).inflate(layoutResId, this, true)
        amountText = findViewById(R.id.amount)
        incrementer = findViewById(R.id.increment_button)
        decrementer = findViewById(R.id.decrement_button)
        setAmount(0)

        incrementer.setOnCounterIncrementedListener(object:
            CounterIncrementerButton.OnCounterIncrementedListener {
            override fun onSingleIncrement() {
                listener?.onAmountIncremented(1)
            }
            override fun onHoldContinued(increments: Int) {
                listener?.onAmountIncremented(CounterUtils.getAmountChangeForHoldIteration(increments))
            }
        })

        decrementer.setOnCounterIncrementedListener(object:
            CounterIncrementerButton.OnCounterIncrementedListener {
            override fun onSingleIncrement() {
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