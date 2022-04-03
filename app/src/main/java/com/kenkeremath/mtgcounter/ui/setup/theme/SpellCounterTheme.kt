package com.kenkeremath.mtgcounter.ui.setup.theme

import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import com.kenkeremath.mtgcounter.R

enum class SpellCounterTheme(
    val id: Long, //Must be unique and must not change
    @StyleRes val resId: Int,
    @StringRes val labelResId: Int,
) {
    NOT_SET(
        id = 0L,
        resId = R.style.LightTheme,
        labelResId = R.string.theme_light,
    ),
    LIGHT(
        id = 1L,
        labelResId = R.string.theme_light,
        resId = R.style.LightTheme,
    ),
    DARK(
        id = 2L,
        labelResId = R.string.theme_dark,
        resId = R.style.DarkTheme,
    );
//    LLANOWAR(
//        id = 3L,
//        labelResId = R.string.theme_llanowar,
//    ),
//    TOLARIA(
//        id = 4L,
//        labelResId = R.string.theme_tolaria,
//    ),
//    LOTUS_PETAL(
//        id = 5L,
//        labelResId = R.string.theme_lotus_petal,
//    ),
//    AETHERHUB(
//        id = 6L,
//        labelResId = R.string.theme_aetherhub,
//    );

    companion object {
        fun fromId(id: Long): SpellCounterTheme {
            return SpellCounterTheme.values().find {
                it.id == id
            } ?: NOT_SET
        }
    }
}