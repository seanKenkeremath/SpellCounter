package com.kenkeremath.mtgcounter.ui.game.dagger

import com.kenkeremath.mtgcounter.model.player.PlayerModel
import com.kenkeremath.mtgcounter.view.counter.edit.CounterSelectionUiModel

data class GamePlayerUiModel(
    var model: PlayerModel,
    var counterSelections: List<CounterSelectionUiModel> = emptyList(),
    var newCounterAdded: Boolean = false
)