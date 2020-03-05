package com.kenkeremath.mtgcounter.model.template

data class GameTemplateModel(
    val name: String, //This is the unique id
    val players: List<PlayerTemplateModel>,
    val playerColors: List<Int>,
    val playerNames: List<String>
)