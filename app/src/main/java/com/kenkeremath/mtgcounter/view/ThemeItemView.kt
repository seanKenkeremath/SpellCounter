package com.kenkeremath.mtgcounter.view

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
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
    private val toolbarSwatch: View
    private val accentSwatch: View
    private val menuButtonSwatch: View
    private val backgroundSwatch: View

    //Either default context or contextThemeWrapper
    private val contextThemeWrapper: Context

    init {
        val themeResId: Int
        val label: String?
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ThemeItemView, 0, 0)
        try {
            themeResId = a.getResourceId(R.styleable.ThemeItemView_scTheme, 0)
            label = a.getString(
                R.styleable.ThemeItemView_label
            )
        } finally {
            a.recycle()
        }
        contextThemeWrapper = if (themeResId != 0) ContextThemeWrapper(context, themeResId) else context
        //Inflate with normal context. we only want to theme the swatches
        LayoutInflater.from(context).inflate(R.layout.item_theme, this, true)

        check = findViewById(R.id.check)
        labelTextView = findViewById(R.id.label)
        toolbarSwatch = findViewById(R.id.toolbar_swatch)
        accentSwatch = findViewById(R.id.accent_swatch)
        menuButtonSwatch = findViewById(R.id.menu_button_swatch)
        backgroundSwatch = findViewById(R.id.background_swatch)

        this.labelTextView.text = label
        //Use regular context for this for better compatibility with theme
        background =
            ColorDrawable(ScThemeUtils.resolveThemeColor(context, R.attr.scBackgroundColor))
        //ripples from theme wrapper
        foreground = ScThemeUtils.resolveThemeDrawable(contextThemeWrapper, R.attr.selectableItemBackground)

        //Create swatches from theme wrapper
        toolbarSwatch.background = ColorDrawable(
            ScThemeUtils.resolveThemeColor(
                contextThemeWrapper, R.attr.scToolbarColor
            )
        )
        accentSwatch.background = ColorDrawable(
            ScThemeUtils.resolveThemeColor(
                contextThemeWrapper, R.attr.scAccentColor
            )
        )
        menuButtonSwatch.background = ColorDrawable(
            ScThemeUtils.resolveThemeColor(
                contextThemeWrapper, R.attr.scMenuEnabledButtonColor
            )
        )
        backgroundSwatch.background = ColorDrawable(
            ScThemeUtils.resolveThemeColor(
                contextThemeWrapper, R.attr.scBackgroundColor
            )
        )

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
        check.visibility = if (selected) VISIBLE else INVISIBLE
    }
}