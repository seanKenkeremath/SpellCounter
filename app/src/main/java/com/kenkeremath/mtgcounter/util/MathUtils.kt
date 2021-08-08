package com.kenkeremath.mtgcounter.util

import kotlin.math.pow
import kotlin.math.sqrt

object MathUtils {
    fun distance(startX: Float, startY: Float, endX: Float, endY: Float): Float {
        return sqrt((endX - startX).pow(2) + (endY - startY).pow(2))
    }
}