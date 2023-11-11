package com.kenkeremath.mtgcounter.view.counter

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ColorInt
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel

class LifeCounterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CounterView(R.layout.view_counter, context, attrs, defStyleAttr) {

    private val counterIconView: CounterIconView = findViewById(R.id.counter_icon_view)

    init {
        counterIconView.setIconDrawable(R.drawable.ic_heart)
    }

    override fun setTextColor(color: Int) {
        super.setTextColor(color)
        counterIconView.setIconDrawable(R.drawable.ic_heart, color)
    }

    fun setCustomCounter(counter: CounterTemplateModel?, @ColorInt iconTint: Int? = null) {
        if (counter == null) {
            counterIconView.setIconDrawable(R.drawable.ic_heart, iconTint)
        } else {
            counterIconView.setContent(
                templateModel = counter,
                renderFullArt = counter.isFullArtImage,
                iconTint = iconTint
            )
        }
    }
}