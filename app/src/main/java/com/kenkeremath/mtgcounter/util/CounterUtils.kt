package com.kenkeremath.mtgcounter.util

import java.util.*

object CounterUtils {
    private const val LARGE_INCREMENT = 5
    private const val SMALL_INCREMENT = 1
    //After this many increments while holding we switch to large increments
    //TODO: this is 0 so holding always results in large increments. Update based on feedback
    private const val LARGE_INCREMENT_THRESHOLD = 0
    fun getAmountChangeForHoldIteration(increments: Int): Int {
        return if (increments <= LARGE_INCREMENT_THRESHOLD) {
            SMALL_INCREMENT
        } else {
            LARGE_INCREMENT
        }
    }
    fun getUniqueId() : Int {
        return UUID.randomUUID().hashCode()
    }
}