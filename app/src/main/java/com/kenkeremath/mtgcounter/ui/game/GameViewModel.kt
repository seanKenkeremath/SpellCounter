package com.kenkeremath.mtgcounter.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kenkeremath.mtgcounter.model.PlayerModel
import com.kenkeremath.mtgcounter.persistence.GameRepository

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    val startingLife = repository.startingLife
    val numberOfPlayers = repository.numberOfPlayers
    val tabletopType = repository.tabletopType

    private val playerList: MutableList<PlayerModel> = mutableListOf()

    private val _players = MutableLiveData<List<PlayerModel>>()
    val players: LiveData<List<PlayerModel>> = _players

    private val _keepScreenOn = MutableLiveData<Boolean>()
    val keepScreenOn: LiveData<Boolean> = _keepScreenOn

    private val _hideNavigation = MutableLiveData<Boolean>()
    val hideNavigation: LiveData<Boolean> = _hideNavigation

    init {
        _keepScreenOn.value = repository.keepScreenOn
        _hideNavigation.value = repository.hideNavigation
        for (i in 0 until numberOfPlayers) {
            playerList.add(
                PlayerModel(
                    id = i,
                    life = startingLife,
                    color = 0
                )
            )
        }
        _players.value = playerList
    }

    fun incrementPlayerLife(index: Int, amount: Int = 1) {
        if (index >= playerList.size || index < 0) {
            return
        }
        val player = playerList[index]
        playerList[index] = player.copy(
            life = player.life + amount
        )
        _players.value = playerList
    }

}