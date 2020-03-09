package com.kenkeremath.mtgcounter.model.template

class GameTemplateModel(
    val id: Int,
    val name: String,
    var startingLifeTotal: Int,
    val players: List<PlayerTemplateModel>,
    val playerColors: List<Int>,
    val playerNames: List<String>
)