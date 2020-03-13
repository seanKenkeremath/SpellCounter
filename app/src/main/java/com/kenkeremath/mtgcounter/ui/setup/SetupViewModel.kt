package com.kenkeremath.mtgcounter.ui.setup

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kenkeremath.mtgcounter.model.PlayerSetupModel
import com.kenkeremath.mtgcounter.model.TabletopType

class SetupViewModel : ViewModel() {

    private val _startingLife = MutableLiveData<Int>()
    val startingLife: LiveData<Int> get() = _startingLife

    private val _keepScreenOn = MutableLiveData<Boolean>()
    val keepScreenOn: LiveData<Boolean> get() = _keepScreenOn

    private val _players = MutableLiveData<List<PlayerSetupModel>>()
    val players: LiveData<List<PlayerSetupModel>> get() = _players

    private val _tabletopType = MutableLiveData<TabletopType>()
    val tabletopType: LiveData<TabletopType> get() = _tabletopType

    fun setNumberOfPlayers(number: Int) {
        val currNumberPlayers = _players.value?.size ?: 0
        val newPlayerList = _players.value?.toMutableList() ?: mutableListOf()
        if (number > currNumberPlayers) {
            val diff = number - currNumberPlayers

            //Add new empty players as necessary
            for (i in 0 until diff) {
                newPlayerList.add(PlayerSetupModel(color = Color.RED))
            }
        } else if (number < currNumberPlayers) {
            val diff = currNumberPlayers - number

            //remove last in order
            for (i in 0 until diff) {
                newPlayerList.removeAt(newPlayerList.size -1)
            }
        }

        _players.value = newPlayerList
    }

    fun setKeepScreenOn(keepScreenOn: Boolean) {
        _keepScreenOn.value = keepScreenOn
    }

    fun setStartingLife(startingLife: Int) {
        _startingLife.value = startingLife
    }

    fun setTabletopType(tabletopType: TabletopType) {
        _tabletopType.value = tabletopType
    }
}
