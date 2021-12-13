package com.kenkeremath.mtgcounter.model.counter

import androidx.annotation.DrawableRes
import com.kenkeremath.mtgcounter.R

/**
 * NOTE: We are using the ordinals of this enum for the default sort order.
 * All IDs should be unique. Comment out any deprecated/removed IDs to maintain proper order/IDs
 */
enum class CounterSymbol(@DrawableRes val resId: Int? = null, val symbolId: Long) {
    NONE(symbolId = CounterSymbol.DEFAULT_ID),
    ISLAND(symbolId = 2L, resId = R.drawable.ic_water),
    SWAMP(symbolId = 3L, resId = R.drawable.ic_skull),
    MOUNTAIN(symbolId = 4L, resId = R.drawable.ic_fire),
    FOREST(symbolId = 5L, resId = R.drawable.ic_tree),
    PLAINS(symbolId = 6L, resId = R.drawable.ic_sun),
    COLORLESS(symbolId = 7L, resId = R.drawable.ic_square),
    SWORD(symbolId = 1L, resId = R.drawable.ic_sword),
    SHIELD(symbolId = 13L, resId = R.drawable.ic_shield),
    STORM(symbolId = 8L, resId = R.drawable.ic_bolt),
    POISON(symbolId = 9L, resId = R.drawable.ic_poison),
    FLASK(symbolId = 10L, resId = R.drawable.ic_flask),
    CROWN(symbolId = 11L, resId = R.drawable.ic_crown),
    CLOCK(symbolId = 12L, resId = R.drawable.ic_clock);

    companion object {
        const val DEFAULT_ID = 0L
    }
}