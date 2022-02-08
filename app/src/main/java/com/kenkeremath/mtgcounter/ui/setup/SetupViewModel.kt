package com.kenkeremath.mtgcounter.ui.setup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.model.player.PlayerColor
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
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
    private val playerColors = PlayerColor.allColors()

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
        /**
         * Add to existing player models or create a sublist if the number has decreased
         */
        val newList = (_setupPlayers.value?.take(number))?.map { player ->
            /**
             * Templates may have been edited or deleted since these models were created.
             * So we should update templates and reset them to default if they no longer
             * exist
             */
            player.copy(template = playerTemplates?.find { it.name == player.template?.name }
                ?: playerTemplates?.find { it.name == PlayerTemplateModel.NAME_DEFAULT }
            )
        }?.toMutableList() ?: mutableListOf()
        while (newList.size < number) {
            newList.add(
                PlayerSetupModel(
                    id = newList.size, //next index
                    //Find the first color that is not currently taken by a player
                    color = playerColors.find { color -> newList.find { it.color == color } == null }
                        ?: PlayerColor.NONE,
                    template = playerTemplates?.find { it.name == PlayerTemplateModel.NAME_DEFAULT }
                )
            )
        }
        _setupPlayers.value = newList
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
        _showCustomizeLayoutButton.value = tabletopType != TabletopType.NONE
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

    /**
     * Generate a new list of player setup models that have additional color counters.
     * Only add color counters that correspond to a player in that game OTHER than the current player.
     *
     * Counters will be given IDs starting at -1 and going backwards to avoid conflicts
     * with database counters
     *
     * This method should be called only one setup is completely finalized since colors can
     * change after the fact
     */
    fun getSetupPlayersWithColorCounters(): List<PlayerSetupModel> {
        return _setupPlayers.value?.let { allPlayers ->
            var currCounterId = -1
            val allGameColors = allPlayers.map {
                it.color
            }.toSet()
            val allGameColorCounters = allGameColors.map {
                val counter = CounterTemplateModel(
                    id = currCounterId,
                    color = it,
                    startingValue = 0,
                )
                currCounterId--
                counter
            }
            allPlayers.map { player ->
                player.template?.let { template ->
                    player.copy(
                        template = template.copy(
                            counters = template.counters.plus(
                                allGameColorCounters.filter {
                                    player.color != it.color
                                }
                            )
                        )
                    )
                } ?: player
            }
        } ?: emptyList()
    }
}
