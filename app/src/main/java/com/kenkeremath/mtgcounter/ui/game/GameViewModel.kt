package com.kenkeremath.mtgcounter.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kenkeremath.mtgcounter.model.PlayerModel
import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.model.template.CounterTemplateModel
import com.kenkeremath.mtgcounter.model.template.PlayerTemplateModel
import com.kenkeremath.mtgcounter.persistence.GameRepository

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    val startingLife = repository.startingLife
    val numberOfPlayers = repository.numberOfPlayers
    val tabletopType = repository.tabletopType

    val players : List<LiveData<PlayerModel>>

    private val _keepScreenOn = MutableLiveData<Boolean>()
    val keepScreenOn: LiveData<Boolean> get() = _keepScreenOn
    private val _hideNavigation = MutableLiveData<Boolean>()
    val hideNavigation: LiveData<Boolean> get() = _hideNavigation


    val allTemplates: LiveData<List<PlayerTemplateModel>> = repository.allPlayerTemplatesEntity
    val allCounters: LiveData<List<CounterTemplateModel>> = repository.allCountersEntity

    init {
        _keepScreenOn.value = repository.keepScreenOn
        _hideNavigation.value = repository.hideNavigation
        players = createPlayers(numberOfPlayers).map { MutableLiveData(it) }
    }

    private fun createPlayers(numberOfPlayers : Int) : List<PlayerModel> {
        val list = mutableListOf<PlayerModel>()
        for (i in 0 until numberOfPlayers) {
            list.add(PlayerModel(
                id = i,
                life = startingLife,
                color = 45)
            )
        }
        return list
    }
}