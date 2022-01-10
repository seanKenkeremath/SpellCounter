package com.kenkeremath.mtgcounter.view.counter

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.counter.CounterModel

class SecondaryCounterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CounterView(R.layout.view_secondary_counter, context, attrs, defStyleAttr) {

    private val image = findViewById<ImageView>(R.id.image)
    private val label = findViewById<TextView>(R.id.label)

    fun setContent(counterModel: CounterModel) {
        if (counterModel.symbolResId == null) {
            if (counterModel.colorResId != null) {
                label.visibility = View.GONE
                image.visibility = View.VISIBLE
                image.setImageResource(R.drawable.ic_circle)
                image.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, counterModel.colorResId)
                )
            } else if (counterModel.name != null) {
                label.visibility = View.VISIBLE
                image.visibility = View.GONE
                label.text = counterModel.name
            }
        } else {
            label.visibility = View.GONE
            image.visibility = View.VISIBLE
            image.setImageResource(counterModel.symbolResId)
            image.imageTintList = ColorStateList.valueOf(
                counterModel.colorResId ?: ContextCompat.getColor(
                    context,
                    R.color.default_icon_tint
                )
            )
        }

        setAmount(counterModel.amount)
    }
}