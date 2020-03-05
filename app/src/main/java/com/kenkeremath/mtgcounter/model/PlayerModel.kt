package com.kenkeremath.mtgcounter.model

data class PlayerModel(
    val id: String,
    val startingLife: Int,
    var life: Int,
    val color: Int,
    val counters: List<CounterModel> = emptyList(),
    val templateId: String? = null
)