package com.kenkeremath.mtgcounter.ui.setup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.kenkeremath.mtgcounter.model.PlayerSetupModel
import com.kenkeremath.mtgcounter.persistence.GameRepository
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SetupViewModelTest {
    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var playersObserver: Observer<List<PlayerSetupModel>>

    @Captor
    lateinit var playersArgumentCaptor: ArgumentCaptor<List<PlayerSetupModel>>

    @Mock
    lateinit var keepScreenOnObserver: Observer<Boolean>

    @Mock
    lateinit var startingLifeObserver: Observer<Int>

    @Mock
    lateinit var gameRepository: GameRepository

    lateinit var viewModel: SetupViewModel

    @Before
    fun setup() {
        viewModel = SetupViewModel(gameRepository)
        viewModel.keepScreenOn.observeForever(keepScreenOnObserver)
        viewModel.startingLife.observeForever(startingLifeObserver)
        viewModel.players.observeForever(playersObserver)
    }

    @Test
    fun checkingKeepScreenOnCheckboxUpdatesLiveData() {
        verify(keepScreenOnObserver, never()).onChanged(ArgumentMatchers.any())
        viewModel.setKeepScreenOn(true)
        verify(keepScreenOnObserver, times(1)).onChanged(true)
    }

    @Test
    fun selectingStartingLifeUpdatesLiveData() {
        verify(startingLifeObserver, never()).onChanged(ArgumentMatchers.any())
        viewModel.setStartingLife(40)
        verify(startingLifeObserver, times(1)).onChanged(40)
    }

    @Test
    fun selectingNumberOfPlayersUpdatesLiveData() {
        verify(playersObserver, never()).onChanged(ArgumentMatchers.any())
        viewModel.setNumberOfPlayers(4)
        verify(playersObserver, times(1)).onChanged(playersArgumentCaptor.capture())

        val players = playersArgumentCaptor.value

        assertEquals(4, players.size)
    }
}