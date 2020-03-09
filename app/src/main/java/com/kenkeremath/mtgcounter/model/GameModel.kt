package com.kenkeremath.mtgcounter.model

data class GameModel(
    val listMode: Boolean = false,
    val keepScreenOn: Boolean = false,
    var startingLife: Int = 0,
    val players: List<PlayerModel> = emptyList(),
    val templateId: Int? = null
)