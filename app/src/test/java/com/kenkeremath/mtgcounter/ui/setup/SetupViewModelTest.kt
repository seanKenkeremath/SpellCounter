package com.kenkeremath.mtgcounter.ui.setup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.persistence.GameRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SetupViewModelTest {
    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var playersObserver: Observer<Int>

    @Mock
    lateinit var keepScreenOnObserver: Observer<Boolean>

    @Mock
    lateinit var startingLifeObserver: Observer<Int>

    @Mock
    lateinit var tabletopTypeObserver: Observer<TabletopType>

    @Mock
    lateinit var gameRepository: GameRepository

    lateinit var viewModel: SetupViewModel

    @Before
    fun setup() {
        Mockito.`when`(gameRepository.keepScreenOn).thenReturn(true)
        Mockito.`when`(gameRepository.startingLife).thenReturn(15)
        Mockito.`when`(gameRepository.numberOfPlayers).thenReturn(7)
        Mockito.`when`(gameRepository.tabletopType).thenReturn(TabletopType.FIVE_ACROSS)

        viewModel = SetupViewModel(gameRepository)
        viewModel.keepScreenOn.observeForever(keepScreenOnObserver)
        viewModel.startingLife.observeForever(startingLifeObserver)
        viewModel.numberOfPlayers.observeForever(playersObserver)
        viewModel.tabletopType.observeForever(tabletopTypeObserver)
    }

    @Test
    fun initializingPullsValuesFromRepository() {
        verify(keepScreenOnObserver, times(1)).onChanged(true)
        verify(startingLifeObserver, times(1)).onChanged(15)
        verify(playersObserver, times(1)).onChanged(7)
        verify(tabletopTypeObserver, times(1)).onChanged(TabletopType.FIVE_ACROSS)

    }

    @Test
    fun checkingKeepScreenOnCheckboxUpdatesLiveData() {
        //Called during initialization to set value
        verify(keepScreenOnObserver, times(1)).onChanged(ArgumentMatchers.any())
        viewModel.setKeepScreenOn(true)
        verify(keepScreenOnObserver, times(2)).onChanged(ArgumentMatchers.any())
        verify(keepScreenOnObserver, atLeastOnce()).onChanged(true)
    }

    @Test
    fun selectingStartingLifeUpdatesLiveData() {
        //Called during initialization to set value
        verify(startingLifeObserver, times(1)).onChanged(ArgumentMatchers.any())
        viewModel.setStartingLife(40)
        verify(startingLifeObserver, times(2)).onChanged(ArgumentMatchers.any())
        verify(startingLifeObserver, atLeastOnce()).onChanged(40)
    }

    @Test
    fun selectingNumberOfPlayersUpdatesLiveData() {
        //Called during initialization to set value
        verify(playersObserver, times(1)).onChanged(ArgumentMatchers.any())
        viewModel.setNumberOfPlayers(4)
        verify(playersObserver, times(2)).onChanged(ArgumentMatchers.any())
        verify(playersObserver, atLeastOnce()).onChanged(4)
    }

    @Test
    fun selectingTabletopTypeUpdatesLiveData() {
        //Called during initialization to set value
        verify(tabletopTypeObserver, times(1)).onChanged(ArgumentMatchers.any())
        viewModel.setTabletopType(TabletopType.LIST)
        verify(tabletopTypeObserver, times(2)).onChanged(ArgumentMatchers.any())
        verify(tabletopTypeObserver, atLeastOnce()).onChanged(TabletopType.LIST)
    }
}