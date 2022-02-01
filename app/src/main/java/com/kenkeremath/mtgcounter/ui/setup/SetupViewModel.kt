package com.kenkeremath.mtgcounter.ui.setup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.model.counter.CounterColor
import com.kenkeremath.mtgcounter.model.player.PlayerSetupModel
import com.kenkeremath.mtgcounter.model.player.PlayerTemplateModel
import com.kenkeremath.mtgcounter.persistence.GameRepository
import com.kenkeremath.mtgcounter.persistence.ProfileRepository
import com.kenkeremath.mtgcounter.ui.settings.profiles.manage.ProfileUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val profilesRepository: ProfileRepository,
) : ViewModel() {

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

    private val _profiles = MutableLiveData<List<ProfileUiModel>>()
    val profiles: LiveData<List<ProfileUiModel>> get() = _profiles

    private val _tabletopTypes = MutableLiveData<List<TabletopLayoutSelectionUiModel>>()
    val tabletopTypes: LiveData<List<TabletopLayoutSelectionUiModel>> get() = _tabletopTypes

    private val _showCustomizeLayoutButton = MutableLiveData<Boolean>(false)
    val showCustomizeLayoutButton: LiveData<Boolean> get() = _showCustomizeLayoutButton

    //Generate 8 unique random colors from list to use for player creation
    private val playerColors = CounterColor.randomColors(8)

    private var playerTemplates: List<PlayerTemplateModel>? = null

    var selectedTabletopType: TabletopType = TabletopType.NONE
        private set

    private val availableTabletopTypes: List<TabletopType>
        get() = TabletopType.getListForNumber(
            _numberOfPlayers.value ?: 0
        )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            profilesRepository.getAllPlayerTemplates()
                .catch {
                    //TODO: error handling?
                }
                .collect {
                    playerTemplates = it
                    _profiles.value = it.map { template ->
                        ProfileUiModel(template)
                    }
                    _startingLife.value = gameRepository.startingLife
                    setTabletopType(gameRepository.tabletopType)
                    setNumberOfPlayers(gameRepository.numberOfPlayers)
                    _keepScreenOn.value = gameRepository.keepScreenOn
                    _hideNavigation.value = gameRepository.hideNavigation
                }
        }
    }

    fun setNumberOfPlayers(number: Int) {
        gameRepository.numberOfPlayers = number
        _numberOfPlayers.value = number
        val newTabletopType =
            if (availableTabletopTypes.contains(selectedTabletopType)) selectedTabletopType
            else availableTabletopTypes[0]
        setTabletopType(newTabletopType)
        _setupPlayers.value =
            List(number) { index ->
                PlayerSetupModel(
                    id = index,
                    color = playerColors[index],
                    template = playerTemplates?.find { it.name == PlayerTemplateModel.NAME_DEFAULT })
            }
    }

    fun setKeepScreenOn(keepScreenOn: Boolean) {
        gameRepository.keepScreenOn = keepScreenOn
        _keepScreenOn.value = keepScreenOn
    }

    fun setHideNavigation(hideNavigation: Boolean) {
        gameRepository.hideNavigation = hideNavigation
        _hideNavigation.value = hideNavigation
    }

    fun setStartingLife(startingLife: Int) {
        gameRepository.startingLife = startingLife
        _startingLife.value = startingLife
    }

    fun setTabletopType(tabletopType: TabletopType) {
        gameRepository.tabletopType = tabletopType
        selectedTabletopType = tabletopType
        _showCustomizeLayoutButton.value =
            tabletopType != TabletopType.LIST && tabletopType != TabletopType.NONE
        _tabletopTypes.value = availableTabletopTypes.map {
            TabletopLayoutSelectionUiModel(
                it,
                it == selectedTabletopType
            )
        }
    }

    fun findSetupPlayerById(id: Int): PlayerSetupModel? {
        return _setupPlayers.value?.find { it.id == id }
    }

    fun updatePlayer(playerSetupModel: PlayerSetupModel) {
        _setupPlayers.value?.let { playerList ->
            playerList.find { it.id == playerSetupModel.id }?.let { existingPlayer ->
                val existingColor = existingPlayer.color
                _setupPlayers.value = playerList.map {
                    if (it.id == playerSetupModel.id) {
                        playerSetupModel
                    } else if (it.color == playerSetupModel.color) {
                        //If there was already a player with this new color, swap colors
                        it.copy(color = existingColor)
                    } else {
                        it
                    }
                }
            }

        }
    }
}
