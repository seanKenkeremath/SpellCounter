package com.kenkeremath.mtgcounter.util

object CounterUtils {
    private const val LARGE_INCREMENT = 5
    private const val SMALL_INCREMENT = 1
    //After this many increments while holding we switch to large increments
    private const val LARGE_INCREMENT_THRESHOLD = 5
    fun getAmountChangeForHoldIteration(increments: Int): Int {
        return if (increments <= LARGE_INCREMENT_THRESHOLD) {
            SMALL_INCREMENT
        } else {
            LARGE_INCREMENT
        }
    }
}