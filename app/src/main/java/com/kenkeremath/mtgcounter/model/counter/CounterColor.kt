package com.kenkeremath.mtgcounter.model.counter

import androidx.annotation.ColorRes
import com.kenkeremath.mtgcounter.R

/**
 * NOTE: We are using the ordinals of this enum for the default sort order.
 * All IDs should be unique. Comment out any deprecated/removed IDs to maintain proper order/IDs
 */
enum class CounterColor(@ColorRes val resId: Int? = null, val colorId: Long) {
    NONE(colorId = CounterSymbol.DEFAULT_ID),
    PINK(colorId = 3L, resId = R.color.light_pink),
    RED(colorId = 4L, resId = R.color.light_red),
    ORANGE(colorId = 5L, resId = R.color.light_orange),
    LIGHT_GREEN(colorId = 6L, resId = R.color.light_green),
    GREEN(colorId = 7L, resId = R.color.green),
    TURKWISE(colorId = 1L, resId = R.color.turkwise),
    BLUE(colorId = 13L, resId = R.color.accent_blue),
    INDIGO(colorId = 2L, resId = R.color.indigo),
    PURPLE(colorId = 8L, resId = R.color.cool_purple);

    companion object {
        const val DEFAULT_ID = 0L
        fun randomColors(amount: Int): List<CounterColor> {
            return allColors().shuffled().take(amount)
        }
        fun allColors(): List<CounterColor> {
            return values().filter { it != NONE }
        }
    }
}