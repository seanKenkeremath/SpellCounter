package com.kenkeremath.mtgcounter.model.template

class PlayerTemplateModel(
    val id: Int,
    val name: String,
    val counters: List<CounterTemplateModel> = emptyList()
)