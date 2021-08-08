package com.kenkeremath.mtgcounter.util

import org.junit.Assert.*
import org.junit.Test

class MathUtilsTest {

    @Test
    fun distance() {
        //xdist = 3, ydist = 4
        assertEquals(5f, MathUtils.distance(0f, 1f, 3f, 5f), .01f)
    }

    @Test
    fun negative_distance() {
        //xdist = -4, ydist = 6
        assertEquals(7.21f, MathUtils.distance(0f, 1f, -4f, 7f), .01f)
    }

    @Test
    fun zero_distance() {
        assertEquals(0f, MathUtils.distance(3f, 4f, 3f, 4f))
    }
}