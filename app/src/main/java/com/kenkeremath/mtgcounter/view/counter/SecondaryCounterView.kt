package com.kenkeremath.mtgcounter.view.counter

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.ColorInt
import com.bumptech.glide.Glide
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.counter.CounterModel

class SecondaryCounterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CounterView(R.layout.view_counter, context, attrs, defStyleAttr) {

    private val counterIconView = findViewById<CounterIconView>(R.id.counter_icon_view)
    private val backgroundImage = findViewById<ImageView>(R.id.background_image)

    fun setContent(counterModel: CounterModel, @ColorInt iconTint: Int? = null) {
        clearBackground()
        counterIconView.setContent(counterModel.template, iconTint = iconTint)
        setAmount(counterModel.amount)
        counterModel.template.uri?.let {
            if (counterModel.template.isFullArtImage) {
                Glide.with(context).load(it)
                    .error(R.drawable.image_error_placeholder)
                    .centerCrop()
                    .into(backgroundImage)
            }
        }
    }

    fun clearBackground() {
        Glide.with(context).clear(backgroundImage)
    }
}