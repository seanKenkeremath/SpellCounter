package com.kenkeremath.mtgcounter.ui.setup

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.kenkeremath.mtgcounter.CoroutineTestRule
import com.kenkeremath.mtgcounter.TestApplication
import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.persistence.*
import com.squareup.moshi.Moshi
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O], application = TestApplication::class)
class SetupViewModelTest {
    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private lateinit var gameRepository: GameRepository

    private lateinit var viewModel: SetupViewModel

    private lateinit var datastore: Datastore

    @MockK
    private lateinit var database: AppDatabase

    @MockK
    private lateinit var templateDao: TemplateDao

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        datastore = DatastoreImpl(ApplicationProvider.getApplicationContext(), Moshi.Builder().build())
        every {
            database.templateDao()
        } returns templateDao
        every {
            templateDao.getCounterTemplates()
        } returns emptyList()
        gameRepository = GameRepositoryImpl(database, datastore)
        datastore.keepScreenOn = false
        datastore.startingLife = 15
        datastore.numberOfPlayers = 3
        datastore.tabletopType = TabletopType.THREE_CIRCLE
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