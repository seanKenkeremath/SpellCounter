package com.kenkeremath.mtgcounter.view.counter

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
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

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.LineColorPickerView, 0, 0)

        try {
            val labelSize = a.getDimension(
                R.styleable.CounterIconView_labelTextSize,
                resources.getDimension(R.dimen.counter_label_default_text_size)
            )
            label.textSize = labelSize
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
            label.visibility = View.GONE
            image.visibility = View.VISIBLE
            image.setImageResource(templateModel.symbol.resId)
            image.imageTintList = ColorStateList.valueOf(
                templateModel.color.resId ?: ContextCompat.getColor(
                    context,
                    R.color.default_icon_tint
                )
            )
        }
    }
}