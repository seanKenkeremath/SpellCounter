package com.kenkeremath.mtgcounter.ui.setup.theme

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import com.kenkeremath.mtgcounter.R

object ScThemeUtils {
    fun resolveThemedTitle(context: Context, theme: SpellCounterTheme): CharSequence {
        val appName = context.getString(R.string.app_name)
        return if (theme != SpellCounterTheme.AETHERHUB) {
            appName
        } else {
            val spannable = SpannableString(appName)
            val spanSection = "Counter"
            val startIndex = spannable.indexOf(spanSection)
            if (startIndex == -1) {
                appName
            } else {
                spannable.setSpan(
                    BackgroundColorSpan(
                        ContextCompat.getColor(
                            context,
                            R.color.aether_hub_primary
                        )
                    ),
                    startIndex,
                    startIndex + spanSection.length,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            context,
                            R.color.dark_mode_black
                        )
                    ),
                    startIndex,
                    startIndex + spanSection.length,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
                spannable
            }
        }
    }

    fun resolveTheme(context: Context, theme: SpellCounterTheme): SpellCounterTheme {
        return if (theme != SpellCounterTheme.NOT_SET) {
            theme
        } else {
            when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    SpellCounterTheme.DARK
                }
                Configuration.UI_MODE_NIGHT_NO -> {
                    SpellCounterTheme.LIGHT
                }
                else -> {
                    SpellCounterTheme.LIGHT
                }
            }
        }
    }

    fun resolveThemeColor(context: Context, @AttrRes themeColorResId: Int): Int {
        val tv = TypedValue()
        context.theme.resolveAttribute(themeColorResId, tv, true)
        return tv.data
    }

    fun resolveThemeDrawable(context: Context, @AttrRes drawableAttrResId: Int): Drawable? {
        val tv = TypedValue()
        context.theme.resolveAttribute(drawableAttrResId, tv, true)
        return ContextCompat.getDrawable(context, tv.resourceId)
    }

    fun isLightTheme(context: Context): Boolean {
        val tv = TypedValue()
        context.theme.resolveAttribute(R.attr.scIsLightTheme, tv, true)
        return tv.data != 0
    }
}