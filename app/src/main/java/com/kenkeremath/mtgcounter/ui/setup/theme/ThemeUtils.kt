package com.kenkeremath.mtgcounter.ui.setup.theme

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes

object ThemeUtils {
    fun resolveThemeColor(context: Context, @AttrRes themeColorResId: Int): Int {
        val tv = TypedValue()
        context.theme.resolveAttribute(themeColorResId, tv, true)
        return tv.data
    }
}