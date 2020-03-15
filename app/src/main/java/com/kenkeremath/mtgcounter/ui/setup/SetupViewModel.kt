package com.kenkeremath.mtgcounter.ui.setup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.model.template.CounterTemplateModel
import com.kenkeremath.mtgcounter.model.template.PlayerTemplateModel
import com.kenkeremath.mtgcounter.persistence.GameRepository
import kotlinx.coroutines.launch

class SetupViewModel constructor(private val repository: GameRepository) : ViewModel() {

    private val _startingLife = MutableLiveData<Int>()
    val startingLife: LiveData<Int> get() = _startingLife

    private val _keepScreenOn = MutableLiveData<Boolean>()
    val keepScreenOn: LiveData<Boolean> get() = _keepScreenOn

    private val _numberOfPlayers = MutableLiveData<Int>()
    val numberOfPlayers: LiveData<Int> get() = _numberOfPlayers

    private val _tabletopType = MutableLiveData<TabletopType>()
    val tabletopType: LiveData<TabletopType> get() = _tabletopType

    //TODO: delete
    val allTemplates: LiveData<List<PlayerTemplateModel>>
    val allCounters: LiveData<List<CounterTemplateModel>>

    init {
        allTemplates = repository.allPlayerTemplatesEntity
        allCounters = repository.allCountersEntity

        _startingLife.value = repository.startingLife
        _numberOfPlayers.value = repository.numberOfPlayers
        _tabletopType.value = repository.tabletopType
        _keepScreenOn.value = repository.keepScreenOn
    }

    //TODO: delete
    fun insert() = viewModelScope.launch {
        val player = PlayerTemplateModel(
            name = "Player1",
            counters = listOf(
                CounterTemplateModel(
                    id = repository.createNewCounterTemplateId(),
                    startingValue = 21,
                    name = "CMD",
                    color = 234
                )
            )
        )
        repository.insert(player)
    }

    fun setNumberOfPlayers(number: Int) {
        repository.numberOfPlayers = number
        _numberOfPlayers.value = number
    }

    fun setKeepScreenOn(keepScreenOn: Boolean) {
        repository.keepScreenOn = keepScreenOn
        _keepScreenOn.value = keepScreenOn
    }

    fun setStartingLife(startingLife: Int) {
        repository.startingLife = startingLife
        _startingLife.value = startingLife
    }

    fun setTabletopType(tabletopType: TabletopType) {
        repository.tabletopType = tabletopType
        _tabletopType.value = tabletopType
    }
}
