package com.kenkeremath.mtgcounter.model.counter

import androidx.annotation.DrawableRes
import com.kenkeremath.mtgcounter.R

/**
 * NOTE: We are using the ordinals of this enum to associate symbols with player templates.
 * Do not remove items from enum or rearrange)
 */
enum class CounterSymbol(@DrawableRes val resId: Int? = null) {
    NONE,
    SWORD(R.drawable.ic_launcher_foreground),
    FOREST(R.drawable.ic_launcher_foreground),
}