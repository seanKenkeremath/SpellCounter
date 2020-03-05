package com.kenkeremath.mtgcounter.model

data class GameModel(
    val numberOfPlayers: Int,
    val listMode: Boolean,
    val players: List<PlayerModel> = emptyList(),
    val templateId: String?
)