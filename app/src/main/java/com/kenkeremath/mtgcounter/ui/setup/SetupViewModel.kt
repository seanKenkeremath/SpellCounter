package com.kenkeremath.mtgcounter.ui.setup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.model.counter.CounterColor
import com.kenkeremath.mtgcounter.model.player.PlayerSetupModel
import com.kenkeremath.mtgcounter.persistence.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(private val repository: GameRepository) : ViewModel() {

    private val _startingLife = MutableLiveData<Int>()
    val startingLife: LiveData<Int> get() = _startingLife

    private val _keepScreenOn = MutableLiveData<Boolean>()
    val keepScreenOn: LiveData<Boolean> get() = _keepScreenOn

    private val _hideNavigation = MutableLiveData<Boolean>()
    val hideNavigation: LiveData<Boolean> get() = _hideNavigation

    private val _numberOfPlayers = MutableLiveData<Int>()
    val numberOfPlayers: LiveData<Int> get() = _numberOfPlayers

    private val _setupPlayers = MutableLiveData<List<PlayerSetupModel>>()
    val setupPlayers: LiveData<List<PlayerSetupModel>> get() = _setupPlayers

    private val _tabletopType = MutableLiveData<TabletopType>()
    val tabletopType: LiveData<TabletopType> get() = _tabletopType

    private val _availableTabletopTypes = MutableLiveData<List<TabletopType>>()
    val availableTabletopTypes: LiveData<List<TabletopType>> get() = _availableTabletopTypes

    private val _showCustomizeLayoutButton = MutableLiveData<Boolean>(false)
    val showCustomizeLayoutButton: LiveData<Boolean> get() = _showCustomizeLayoutButton

    init {
        _startingLife.value = repository.startingLife
        setTabletopType(repository.tabletopType)
        setNumberOfPlayers(repository.numberOfPlayers)
        _keepScreenOn.value = repository.keepScreenOn
        _hideNavigation.value = repository.hideNavigation
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
        val currentTabletopType = _tabletopType.value
        if (!availableTabletopTypes.contains(currentTabletopType)) {
            setTabletopType(TabletopType.getListForNumber(number)[0])
        }
        val colors = CounterColor.randomColors(number)
        _setupPlayers.value =
            List(number) { index -> PlayerSetupModel(colorResId = colors[index].resId) }
    }

    fun setKeepScreenOn(keepScreenOn: Boolean) {
        repository.keepScreenOn = keepScreenOn
        _keepScreenOn.value = keepScreenOn
    }

    fun setHideNavigation(hideNavigation: Boolean) {
        repository.hideNavigation = hideNavigation
        _hideNavigation.value = hideNavigation
    }

    fun setStartingLife(startingLife: Int) {
        repository.startingLife = startingLife
        _startingLife.value = startingLife
    }

    fun setTabletopType(tabletopType: TabletopType) {
        repository.tabletopType = tabletopType
        _tabletopType.value = tabletopType
        _showCustomizeLayoutButton.value =
            tabletopType != TabletopType.LIST && tabletopType != TabletopType.NONE
    }
}
