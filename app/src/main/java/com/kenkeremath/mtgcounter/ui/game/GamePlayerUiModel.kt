package com.kenkeremath.mtgcounter.ui.game

import com.kenkeremath.mtgcounter.model.player.PlayerModel
import com.kenkeremath.mtgcounter.view.counter.edit.CounterSelectionUiModel

data class GamePlayerUiModel(
    var model: PlayerModel,
    var counterSelections: List<CounterSelectionUiModel> = emptyList(),
    var newCounterAdded: Boolean = false,
    var pullToReveal: Boolean = false,
    var currentMenu: Menu = Menu.MAIN
) {
    enum class Menu {
        MAIN,
        EDIT_COUNTERS
    }
}