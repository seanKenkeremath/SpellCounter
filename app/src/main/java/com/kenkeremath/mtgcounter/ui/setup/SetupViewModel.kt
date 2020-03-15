package com.kenkeremath.mtgcounter.ui.setup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.persistence.GameRepository

class SetupViewModel constructor(private val repository: GameRepository) : ViewModel() {

    private val _startingLife = MutableLiveData<Int>()
    val startingLife: LiveData<Int> get() = _startingLife

    private val _keepScreenOn = MutableLiveData<Boolean>()
    val keepScreenOn: LiveData<Boolean> get() = _keepScreenOn

    private val _numberOfPlayers = MutableLiveData<Int>()
    val numberOfPlayers: LiveData<Int> get() = _numberOfPlayers

    private val _tabletopType = MutableLiveData<TabletopType>()
    val tabletopType: LiveData<TabletopType> get() = _tabletopType

    private val _availableTabletopTypes = MutableLiveData<List<TabletopType>>()
    val availableTabletopTypes: LiveData<List<TabletopType>> get() = _availableTabletopTypes

    init {
        _startingLife.value = repository.startingLife
        _numberOfPlayers.value = repository.numberOfPlayers
        _tabletopType.value = repository.tabletopType
        _keepScreenOn.value = repository.keepScreenOn
        _availableTabletopTypes.value = TabletopType.getListForNumber(repository.numberOfPlayers)
    }

//    //TODO: delete
//    fun insert() = viewModelScope.launch {
//        val player = PlayerTemplateModel(
//            name = "Player1",
//            counters = listOf(
//                CounterTemplateModel(
//                    id = repository.createNewCounterTemplateId(),
//                    startingValue = 21,
//                    name = "CMD",
//                    color = 234
//                )
//            )
//        )
//        repository.insert(player)
//    }

    fun setNumberOfPlayers(number: Int) {
        repository.numberOfPlayers = number
        _numberOfPlayers.value = number
        val availableTabletopTypes = TabletopType.getListForNumber(number)
        _availableTabletopTypes.value = availableTabletopTypes
        if (availableTabletopTypes.isNotEmpty()) {
            setTabletopType(TabletopType.getListForNumber(number)[0])
        }
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
