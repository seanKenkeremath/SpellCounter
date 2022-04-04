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
    MOX_EMERALD(
        id = 3L,
        resId = R.style.MoxEmerald,
    ),
    LOTUS_PETAL(
        id = 5L,
        resId = R.style.LotusPetal,
    ),
    AETHERHUB(
        id = 6L,
        resId = R.style.Aetherhub
    ),
    PINK(
        id = 7L,
        resId = R.style.PinkTheme
    );

    companion object {
        fun fromId(id: Long): SpellCounterTheme {
            return values().find {
                it.id == id
            } ?: NOT_SET
        }
    }
}