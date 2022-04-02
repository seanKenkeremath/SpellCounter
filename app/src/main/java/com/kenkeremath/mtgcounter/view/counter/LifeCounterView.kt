package com.kenkeremath.mtgcounter.view.counter

import android.content.Context
import android.util.AttributeSet
import com.kenkeremath.mtgcounter.R

class LifeCounterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CounterView(R.layout.view_counter, context, attrs, defStyleAttr) {
    val counterIconView: CounterIconView = findViewById(R.id.counter_icon_view)
    init {
        counterIconView.setIconDrawable(R.drawable.ic_heart)
    }

    override fun setTextColor(color: Int) {
        super.setTextColor(color)
        counterIconView.setIconDrawable(R.drawable.ic_heart, color)
    }
}