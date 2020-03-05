package com.kenkeremath.mtgcounter.model

data class CounterModel(
    val id: String,
    val startingValue: Int,
    var value: Int,
    val name: String?,
    val color: Int,
    val templateId : String? = null
)