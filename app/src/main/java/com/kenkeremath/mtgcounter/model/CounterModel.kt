package com.kenkeremath.mtgcounter.model

data class CounterModel(
    val id: Int,
    val startingValue: Int = 0,
    var value: Int = 0,
    val name: String? = null,
    val color: Int = 0,
    val templateId : Int? = null
)