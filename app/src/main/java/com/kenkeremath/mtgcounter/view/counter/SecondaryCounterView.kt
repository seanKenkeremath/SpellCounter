package com.kenkeremath.mtgcounter.view.counter

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.ColorInt
import com.bumptech.glide.Glide
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.counter.CounterModel
import com.kenkeremath.mtgcounter.ui.setup.theme.ScThemeUtils

class SecondaryCounterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CounterView(R.layout.view_counter, context, attrs, defStyleAttr) {

    private val counterIconView = findViewById<CounterIconView>(R.id.counter_icon_view)
    private val backgroundImage = findViewById<ImageView>(R.id.background_image)

    /**
     * Set data to this counter. This updates amount and icon. It also handles
     * dark/light mode theming
     */
    fun setContent(counterModel: CounterModel, @ColorInt playerTint: Int) {
        if (ScThemeUtils.isLightTheme(context)) {
            counterIconView.setContent(counterModel.template)
        } else {
            counterIconView.setContent(counterModel.template, iconTint = playerTint)
            setTextColor(playerTint)
        }
        clearBackground()
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