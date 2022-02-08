package com.kenkeremath.mtgcounter.model.player

import androidx.annotation.ColorRes
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.counter.CounterSymbol

/**
 * NOTE: We are using the ordinals of this enum for the default sort order.
 * All IDs should be unique. Comment out any deprecated/removed IDs to maintain proper order/IDs
 */
enum class PlayerColor(@ColorRes val resId: Int? = null, val colorId: Long) {
    NONE(colorId = CounterSymbol.DEFAULT_ID),
    BLUE(colorId = 13L, resId = R.color.accent_blue),
    RED(colorId = 4L, resId = R.color.light_red),
    TURKWISE(colorId = 1L, resId = R.color.turkwise),
    PURPLE(colorId = 8L, resId = R.color.cool_purple),
    ORANGE(colorId = 5L, resId = R.color.light_orange),
    GREEN(colorId = 7L, resId = R.color.green),
    INDIGO(colorId = 2L, resId = R.color.indigo),
    LIGHT_GREEN(colorId = 6L, resId = R.color.light_green),
    PINK(colorId = 3L, resId = R.color.light_pink),
    WHITE(colorId = 14L, resId = R.color.white);

    companion object {
        const val DEFAULT_ID = 0L
        fun randomColors(amount: Int): List<PlayerColor> {
            return allColors().shuffled().take(amount)
        }

        fun allColors(): List<PlayerColor> {
            return values().filter { it != NONE }
        }
    }
}