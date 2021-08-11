package com.kenkeremath.mtgcounter.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kenkeremath.mtgcounter.model.CounterModel
import com.kenkeremath.mtgcounter.model.PlayerModel
import com.kenkeremath.mtgcounter.persistence.GameRepository
import com.kenkeremath.mtgcounter.util.CounterUtils

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    val startingLife = repository.startingLife
    val numberOfPlayers = repository.numberOfPlayers
    val tabletopType = repository.tabletopType

    private val playerMap: MutableMap<Int, PlayerModel> = mutableMapOf()

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
            playerMap[i] = PlayerModel(
                id = i,
                life = startingLife,
                color = 0,
            )
        }
        _players.value = playerMap.values.toList()
    }

    fun incrementPlayerLife(playerId: Int, lifeDifference: Int = 1) {
        playerMap[playerId]?.let {
            playerMap[playerId] = it.copy(
                life = it.life + lifeDifference
            )
            _players.value = playerMap.values.toList()
        }
    }

    fun incrementCounter(playerId: Int, counterId: Int, amountDifference: Int = 1) {
        playerMap[playerId]?.let { player: PlayerModel ->
            player.counters.find {
                it.id == counterId
            }?.let { counter: CounterModel ->
                val counterIndex = player.counters.indexOf(counter)
                val countersList = player.counters.toMutableList()
                countersList[counterIndex] =
                    counter.copy(amount = counter.amount + amountDifference)
                playerMap[playerId] = player.copy(counters = countersList)
                _players.value = playerMap.values.toList()
            }
        }
    }

    //TODO
    fun addCounter(playerId: Int) {
        playerMap[playerId]?.let {
            val counters = it.counters.toMutableList()
            counters.add(CounterModel(CounterUtils.getUniqueId()))
            playerMap[playerId] = it.copy(counters = counters)
            _players.value = playerMap.values.toList()
        }
    }
}