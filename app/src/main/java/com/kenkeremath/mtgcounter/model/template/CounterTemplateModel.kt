package com.kenkeremath.mtgcounter.model.template

data class CounterTemplateModel(
    val id: String,
    val startingValue: Int,
    val name: String?,
    val color: Int
)