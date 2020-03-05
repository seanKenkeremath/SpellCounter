package com.kenkeremath.mtgcounter.model.template

data class PlayerTemplateModel(
    val id: String,
    val startingLife: Int,
    val counters: List<CounterTemplateModel> = emptyList()
)