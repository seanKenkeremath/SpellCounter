package com.kenkeremath.mtgcounter.view.counter

import android.content.Context
import android.util.AttributeSet
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.counter.CounterModel

class SecondaryCounterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CounterView(R.layout.view_counter, context, attrs, defStyleAttr) {

    private val counterIconView = findViewById<CounterIconView>(R.id.counter_icon_view)

    fun setContent(counterModel: CounterModel) {
        counterIconView.setContent(counterModel.template)
        setAmount(counterModel.amount)
    }
}