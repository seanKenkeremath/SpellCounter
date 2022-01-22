package com.kenkeremath.mtgcounter.ui.game

import androidx.lifecycle.*
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.model.counter.CounterModel
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.model.player.PlayerModel
import com.kenkeremath.mtgcounter.model.player.PlayerSetupModel
import com.kenkeremath.mtgcounter.persistence.GameRepository
import com.kenkeremath.mtgcounter.view.counter.edit.CounterSelectionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val repository: GameRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val setupPlayers = savedStateHandle.get<List<PlayerSetupModel>>(GameActivity.ARGS_SETUP_PLAYERS)
        ?: throw IllegalArgumentException("PlayerSetupModels must be passed in intent")

    val startingLife = repository.startingLife
    val tabletopType = repository.tabletopType

    private var availableCounters: List<CounterTemplateModel> = listOf()

    private val playerMap: MutableMap<Int, GamePlayerUiModel> = mutableMapOf()

    private val _players = MutableLiveData<List<GamePlayerUiModel>>()
    val players: LiveData<List<GamePlayerUiModel>> = _players

    private val _keepScreenOn = MutableLiveData<Boolean>()
    val keepScreenOn: LiveData<Boolean> = _keepScreenOn

    private val _hideNavigation = MutableLiveData<Boolean>()
    val hideNavigation: LiveData<Boolean> = _hideNavigation

    /**
     * Maps player id to a list of selected counter template ids
     */
    private val pendingCounterSelectionMap: MutableMap<Int, MutableSet<Int>> = mutableMapOf()

    init {
        _keepScreenOn.value = repository.keepScreenOn
        _hideNavigation.value = repository.hideNavigation
        viewModelScope.launch {
            repository.getAllCounters().collect {
                availableCounters = it
                for (i in 0 until setupPlayers?.size) {
                    val playerId = i

                    val player = GamePlayerUiModel(
                        PlayerModel(
                            id = playerId,
                            life = startingLife,
                            colorResId = setupPlayers[i].colorResId ?: R.color.white,
                        ),
                        //TODO: option from repo
                        pullToReveal = tabletopType != TabletopType.LIST
                    )
                    playerMap[i] = player

                    /**
                     * Make sure pending map of selection changes is in sync with whatever templates
                     * the player starts with
                     */
                    pendingCounterSelectionMap[player.model.id] =
                        player.model.counters.map { counter ->
                            counter.template.id
                        }.toMutableSet()

                    playerMap[i]?.counterSelections = generateSelectionUiModelsForPlayer(playerId)
                }
                _players.value = playerMap.values.toList()
            }
        }
    }

    fun incrementPlayerLife(playerId: Int, lifeDifference: Int = 1) {
        playerMap[playerId]?.let {
            playerMap[playerId] = it.copy(
                model = it.model.copy(
                    life = it.model.life + lifeDifference
                )
            )
            _players.value = playerMap.values.toList()
        }
    }

    fun incrementCounter(playerId: Int, counterId: Int, amountDifference: Int = 1) {
        playerMap[playerId]?.let { player: GamePlayerUiModel ->
            player.model.counters.find {
                it.template.id == counterId
            }?.let { counter: CounterModel ->
                val counterIndex = player.model.counters.indexOf(counter)
                val countersList = player.model.counters.toMutableList()
                countersList[counterIndex] =
                    counter.copy(amount = counter.amount + amountDifference)
                playerMap[playerId] =
                    player.copy(model = player.model.copy(counters = countersList))
                _players.value = playerMap.values.toList()
            }
        }
    }

    fun editCounters(playerId: Int) {
        playerMap[playerId]?.let { player ->
            playerMap[playerId] = player.copy(currentMenu = GamePlayerUiModel.Menu.EDIT_COUNTERS)
        }
        _players.value = playerMap.values.toList()
    }

    fun closeSubMenu(playerId: Int) {
        playerMap[playerId]?.let { player ->
            playerMap[playerId] = player.copy(currentMenu = GamePlayerUiModel.Menu.MAIN)
        }
        _players.value = playerMap.values.toList()
    }

    fun selectCounter(playerId: Int, counterTemplateId: Int) {
        if (!pendingCounterSelectionMap.containsKey(playerId)) {
            pendingCounterSelectionMap[playerId] = mutableSetOf()
        }
        pendingCounterSelectionMap[playerId]?.add(counterTemplateId)
        playerMap[playerId]?.counterSelections = generateSelectionUiModelsForPlayer(playerId)
        _players.value = playerMap.values.toList()
    }

    fun deselectCounter(playerId: Int, counterTemplateId: Int) {
        if (!pendingCounterSelectionMap.containsKey(playerId)) {
            pendingCounterSelectionMap[playerId] = mutableSetOf()
        }
        pendingCounterSelectionMap[playerId]?.remove(counterTemplateId)
        playerMap[playerId]?.counterSelections = generateSelectionUiModelsForPlayer(playerId)
        _players.value = playerMap.values.toList()
    }

    /**
     * Does a diff of pending changes and currently added counters to see which need to be removed
     * or added
     */
    fun confirmCounterChanges(playerId: Int) {
        playerMap[playerId]?.let { uiModel ->
            pendingCounterSelectionMap[playerId]?.let { pendingCounterSelection ->
                val newCountersList = uiModel.model.counters.toMutableList()
                //remove deselected counters
                val itr = newCountersList.iterator()
                while (itr.hasNext()) {
                    val counter = itr.next()
                    if (!pendingCounterSelection.contains(counter.template.id)) {
                        itr.remove()
                    }
                }

                var newCounter = false

                //add newly selected counters from templates
                for (templateId in pendingCounterSelection) {
                    availableCounters.find { it.id == templateId }?.let { template ->
                        //avoid adding duplicates
                        if (newCountersList.find { it.template.id == template.id } == null) {
                            newCounter = true
                            newCountersList.add(CounterModel(template = template))
                        }
                    }
                }

                uiModel.model = uiModel.model.copy(counters = newCountersList)
                uiModel.newCounterAdded = newCounter
                _players.value = playerMap.values.toList()
            }
        }
    }

    /**
     * Removes pending changes and resets selection state to the player's currently added
     * counters
     */
    fun cancelCounterChanges(playerId: Int) {
        pendingCounterSelectionMap[playerId]?.clear()
        playerMap[playerId]?.model?.counters?.let {
            for (counterModel in it) {
                pendingCounterSelectionMap[playerId]?.add(counterModel.template.id)
            }
        }
        playerMap[playerId]?.counterSelections = generateSelectionUiModelsForPlayer(playerId)
        _players.value = playerMap.values.toList()
    }

    /**
     * takes current/pending selections and creates selection uiModels that are passed to the edit
     * counters recyclerview
     */
    private fun generateSelectionUiModelsForPlayer(playerId: Int): List<CounterSelectionUiModel> {
        return playerMap[playerId]?.let { player ->
            availableCounters.filter {
                !(it.color.resId != null && it.color.resId == player.model.colorResId)
            }.map {
                CounterSelectionUiModel(
                    it,
                    pendingCounterSelectionMap[playerId]?.contains(it.id) == true
                )
            }

        } ?: emptyList()
    }
}