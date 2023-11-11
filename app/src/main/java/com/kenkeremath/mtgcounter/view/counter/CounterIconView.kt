package com.kenkeremath.mtgcounter.view.counter

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.bumptech.glide.Glide
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.ui.setup.theme.ScThemeUtils

class CounterIconView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val image: ImageView
    private val label: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_counter_icon, this, true)
        image = findViewById(R.id.image)
        label = findViewById(R.id.label)

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.CounterIconView, 0, 0)
        try {
            val labelMaxSize = a.getDimensionPixelSize(
                R.styleable.CounterIconView_labelMaxTextSize,
                resources.getDimensionPixelSize(R.dimen.counter_label_default_max_text_size)
            )
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                label,
                resources.getDimensionPixelSize(R.dimen.counter_label_min_text_size),
                labelMaxSize,
                1,
                TypedValue.COMPLEX_UNIT_PX,
            )
        } finally {
            a.recycle()
        }
    }

    /**
     * drawFullArt specifices whether this view should handle rendering counters marked as full art.
     * In most situations, they will be roundered as the background outside of this view.
     *
     * iconTint is used in dark mode as a default tint color instead of the primary text color.
     */
    fun setContent(
        templateModel: CounterTemplateModel,
        renderFullArt: Boolean = false,
        @ColorInt iconTint: Int? = null
    ) {
        label.visibility = View.GONE
        image.visibility = View.GONE
        clearImage()
        if (templateModel.symbol.resId == null) {
            if (templateModel.color.resId != null) {
                image.visibility = View.VISIBLE
                image.setImageResource(R.drawable.ic_circle)
                image.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, templateModel.color.resId)
                )
            } else if (templateModel.name != null) {
                label.visibility = View.VISIBLE
                label.text = templateModel.name
                iconTint?.let {
                    label.setTextColor(it)
                }
            } else if (templateModel.isFullArtImage && !renderFullArt) {
                //Image may be rendered outside of this view
            } else if (templateModel.uri != null) {
                image.visibility = View.VISIBLE
                image.imageTintList = null
                Glide.with(context).load(templateModel.uri)
                    .error(R.drawable.image_error_placeholder)
                    .fitCenter()
                    .into(image)
            }
        } else {
            setIconDrawable(
                templateModel.symbol.resId,
                iconTint ?: templateModel.color.resId?.let { ContextCompat.getColor(context, it) })
        }
    }

    fun clearImage() {
        Glide.with(context).clear(image)
        image.setImageDrawable(null)
    }

    fun setIconDrawable(@DrawableRes drawableResId: Int, @ColorInt tint: Int? = null) {
        label.visibility = View.GONE
        image.visibility = View.VISIBLE
        image.setImageResource(drawableResId)
        image.imageTintList = ColorStateList.valueOf(
            tint ?: ScThemeUtils.resolveThemeColor(context, R.attr.scTextColorPrimary)
        )
    }
}