package com.kenkeremath.mtgcounter.view.counter

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.ColorInt
import com.bumptech.glide.Glide
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.ui.setup.theme.ScThemeUtils

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

    /**
     * Set data to this counter. This updates amount and icon. It also handles
     * dark/light mode theming
     */
    fun setCustomCounter(counter: CounterTemplateModel?, @ColorInt playerTint: Int) {
        clearBackground()
        val resolvedIconTint: Int? = if (ScThemeUtils.isLightTheme(context)) {
            null
        } else {
            playerTint
        }
        // Only set this for dark mode
        resolvedIconTint?.let {
            setTextColor(it)
        }
        if (counter == null) {
            counterIconView.setIconDrawable(R.drawable.ic_heart, resolvedIconTint)
        } else {
            counterIconView.setContent(
                templateModel = counter,
                iconTint = resolvedIconTint
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