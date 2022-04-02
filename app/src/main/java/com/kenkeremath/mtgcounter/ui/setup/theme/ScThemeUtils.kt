package com.kenkeremath.mtgcounter.ui.setup.theme

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import com.kenkeremath.mtgcounter.R

object ScThemeUtils {
    fun resolveThemeColor(context: Context, @AttrRes themeColorResId: Int): Int {
        val tv = TypedValue()
        context.theme.resolveAttribute(themeColorResId, tv, true)
        return tv.data
    }

    fun isLightTheme(context: Context): Boolean {
        val tv = TypedValue()
        context.theme.resolveAttribute(R.attr.scIsLightTheme, tv, true)
        return tv.data != 0
    }
}