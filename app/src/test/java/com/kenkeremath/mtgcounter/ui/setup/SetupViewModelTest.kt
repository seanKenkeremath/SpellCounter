package com.kenkeremath.mtgcounter.ui.setup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.persistence.GameRepository
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SetupViewModelTest {
    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var gameRepository: GameRepository

    private lateinit var viewModel: SetupViewModel

    @Before
    fun setup() {
        Mockito.`when`(gameRepository.keepScreenOn).thenReturn(false)
        Mockito.`when`(gameRepository.startingLife).thenReturn(15)
        Mockito.`when`(gameRepository.numberOfPlayers).thenReturn(3)
        Mockito.`when`(gameRepository.tabletopType).thenReturn(TabletopType.THREE_CIRCLE)

        viewModel = SetupViewModel(gameRepository)
    }

    @Test
    fun initialize_pulls_values_from_repository() {
        assertEquals(false, viewModel.keepScreenOn.value)
        assertEquals(15, viewModel.startingLife.value)
        assertEquals(3, viewModel.numberOfPlayers.value)
        assertEquals(TabletopType.THREE_CIRCLE, viewModel.tabletopType.value)
        val expectedSetOfTabletopTypes = setOf(
            TabletopType.THREE_ACROSS,
            TabletopType.THREE_CIRCLE,
            TabletopType.LIST
        )
        assertEquals(expectedSetOfTabletopTypes, viewModel.availableTabletopTypes.value?.toSet())
    }

    @Test
    fun set_keep_screen_on() {
        viewModel.setKeepScreenOn(true)
        assertEquals(true, viewModel.keepScreenOn.value)
    }

    @Test
    fun select_starting_life() {
        viewModel.setStartingLife(40)
        assertEquals(40, viewModel.startingLife.value)
    }

    @Test
    fun select_number_of_players() {
        viewModel.setNumberOfPlayers(4)
        assertEquals(4, viewModel.numberOfPlayers.value)
    }

    @Test
    fun select_number_of_players_maintains_compatible_tabletop_type() {
        viewModel.setTabletopType(TabletopType.LIST)
        assertEquals(TabletopType.LIST, viewModel.tabletopType.value)
        viewModel.setNumberOfPlayers(5)
        assertEquals(TabletopType.LIST, viewModel.tabletopType.value)
    }

    @Test
    fun select_number_of_players_changes_tabletop_type_when_incompatible() {
        viewModel.setTabletopType(TabletopType.THREE_ACROSS)
        assertEquals(TabletopType.THREE_ACROSS, viewModel.tabletopType.value)
        viewModel.setNumberOfPlayers(5)
        assertEquals(TabletopType.FIVE_CIRCLE, viewModel.tabletopType.value)
    }

    @Test
    fun select_tabletop_type() {
        viewModel.setTabletopType(TabletopType.LIST)
        assertEquals(TabletopType.LIST, viewModel.tabletopType.value)
    }
}