package com.kenkeremath.mtgcounter.util

import java.util.*

fun getUniqueId() : Int {
    return UUID.randomUUID().hashCode()
}