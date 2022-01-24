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
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel

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

    fun setContent(templateModel: CounterTemplateModel) {
        if (templateModel.symbol.resId == null) {
            if (templateModel.color.resId != null) {
                label.visibility = View.GONE
                image.visibility = View.VISIBLE
                image.setImageResource(R.drawable.ic_circle)
                image.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, templateModel.color.resId)
                )
            } else if (templateModel.name != null) {
                label.visibility = View.VISIBLE
                image.visibility = View.GONE
                label.text = templateModel.name
            }
        } else {
            setIconDrawable(templateModel.symbol.resId, templateModel.color.resId)
        }
    }

    fun setIconDrawable(@DrawableRes drawableResId: Int, @ColorRes tintResId: Int? = null) {
        label.visibility = View.GONE
        image.visibility = View.VISIBLE
        image.setImageResource(drawableResId)
        image.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                tintResId ?: R.color.default_icon_tint
            )
        )
    }
}