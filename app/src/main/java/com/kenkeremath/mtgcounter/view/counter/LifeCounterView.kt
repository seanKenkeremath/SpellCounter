package com.kenkeremath.mtgcounter.view.counter

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.ColorInt
import com.bumptech.glide.Glide
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel

class LifeCounterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CounterView(R.layout.view_counter, context, attrs, defStyleAttr) {

    private val counterIconView: CounterIconView = findViewById(R.id.counter_icon_view)
    private val backgroundImage = findViewById<ImageView>(R.id.background_image)

    init {
        counterIconView.setIconDrawable(R.drawable.ic_heart)
    }

    override fun setTextColor(color: Int) {
        super.setTextColor(color)
        counterIconView.setIconDrawable(R.drawable.ic_heart, color)
    }

    fun setCustomCounter(counter: CounterTemplateModel?, @ColorInt iconTint: Int? = null) {
        clearBackground()
        if (counter == null) {
            counterIconView.setIconDrawable(R.drawable.ic_heart, iconTint)
        } else {
            counterIconView.setContent(
                templateModel = counter,
                iconTint = iconTint
            )
            counter.uri?.let {
                if (counter.isFullArtImage) {
                    Glide.with(context).load(it)
                        .error(R.drawable.image_error_placeholder)
                        .centerCrop()
                        .into(backgroundImage)
                }
            }
        }
    }

    fun clearBackground() {
        Glide.with(context).clear(backgroundImage)
    }
}