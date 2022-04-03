package com.kenkeremath.mtgcounter.ui.setup.theme

import androidx.annotation.StyleRes
import com.kenkeremath.mtgcounter.R

enum class SpellCounterTheme(
    val id: Long, //Must be unique and must not change
    @StyleRes val resId: Int,
) {
    NOT_SET(
        id = 0L,
        resId = R.style.LightTheme,
    ),
    LIGHT(
        id = 1L,
        resId = R.style.LightTheme,
    ),
    DARK(
        id = 2L,
        resId = R.style.DarkTheme,
    ),
    LLANOWAR(
        id = 3L,
        resId = R.style.Llanowar,
    ),
    LOTUS_PETAL(
        id = 5L,
        resId = R.style.LotusPetal,
    ),
    AETHERHUB(
        id = 6L,
        resId = R.style.Aetherhub
    );
//    TOLARIA(
//        id = 4L,
//        labelResId = R.string.theme_tolaria,
//    ),
//    LOTUS_PETAL(
//        id = 5L,
//        labelResId = R.string.theme_lotus_petal,
//    ),


    companion object {
        fun fromId(id: Long): SpellCounterTheme {
            return values().find {
                it.id == id
            } ?: NOT_SET
        }
    }
}