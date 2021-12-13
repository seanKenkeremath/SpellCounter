package com.kenkeremath.mtgcounter.view.counter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.counter.CounterModel

class SecondaryCounterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CounterView(R.layout.view_secondary_counter, context, attrs, defStyleAttr) {

    private val image = findViewById<ImageView>(R.id.image)

    fun setContent(counterModel: CounterModel) {
        if (counterModel.symbolResId == null) {
            if (counterModel.color != null) {
                image.setImageResource(R.drawable.ic_circle)
                image.imageTintList = ColorStateList.valueOf(
                    counterModel.color
                )
            }
        } else {
            image.setImageResource(counterModel.symbolResId)
            image.imageTintList = ColorStateList.valueOf(
                counterModel.color ?: ContextCompat.getColor(context, R.color.default_icon_tint)
            )
        }

        setAmount(counterModel.amount)
    }
}