package com.kenkeremath.mtgcounter.view

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.ui.setup.theme.ScThemeUtils

class ThemeItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val check: ImageView
    private val labelTextView: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.item_theme, this, true)
        check = findViewById(R.id.check)
        labelTextView = findViewById(R.id.label)
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ThemeItemView, 0, 0)
        try {
            val label = a.getString(
                R.styleable.ThemeItemView_label
            )
            this.labelTextView.text = label
        } finally {
            a.recycle()
        }

        background =
            ColorDrawable(ScThemeUtils.resolveThemeColor(context, R.attr.scBackgroundColor))
        foreground = ScThemeUtils.resolveThemeDrawable(context, R.attr.selectableItemBackground)
        gravity = Gravity.CENTER_VERTICAL
        orientation = HORIZONTAL
        setPadding(resources.getDimensionPixelSize(R.dimen.default_padding))
    }

    var label: CharSequence
        get() = this.labelTextView.text
        set(value) {
            this.labelTextView.text = value
        }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        check.visibility = if (selected) VISIBLE else GONE
    }
}