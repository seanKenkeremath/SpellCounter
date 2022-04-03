package com.kenkeremath.mtgcounter.ui.setup

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.kenkeremath.mtgcounter.CoroutineTestRule
import com.kenkeremath.mtgcounter.TestApplication
import com.kenkeremath.mtgcounter.data.MockDatabaseFactory
import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.model.player.PlayerColor
import com.kenkeremath.mtgcounter.model.player.PlayerProfileModel
import com.kenkeremath.mtgcounter.persistence.*
import io.mockk.MockKAnnotations
import junit.framework.Assert.*
import org.junit.After
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

    private lateinit var profilesRepository: ProfileRepository

    private lateinit var database: AppDatabase

    private lateinit var viewModel: SetupViewModel

    private lateinit var datastore: Datastore

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        datastore =
            DatastoreImpl(ApplicationProvider.getApplicationContext())
        database = MockDatabaseFactory.createDefaultTemplateDatabase()

        gameRepository = GameRepositoryImpl(datastore)
        profilesRepository = ProfileRepositoryImpl(
            database,
            datastore,
            dispatcherProvider = coroutinesTestRule.testDispatcherProvider
        )
        datastore.keepScreenOn = false
        datastore.startingLife = 15
        datastore.numberOfPlayers = 3
        datastore.tabletopType = TabletopType.THREE_CIRCLE
        viewModel = SetupViewModel(gameRepository, profilesRepository)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun initialize_pulls_values_from_repository() {
        assertEquals(false, viewModel.keepScreenOn.value)
        assertEquals(15, viewModel.startingLife.value)
        assertEquals(3, viewModel.numberOfPlayers.value)
        assertEquals(TabletopType.THREE_CIRCLE, viewModel.selectedTabletopType)
        val expectedSetOfTabletopTypes = setOf(
            TabletopLayoutSelectionUiModel(
                TabletopType.THREE_ACROSS, selected = false
            ),
            TabletopLayoutSelectionUiModel(
                TabletopType.THREE_CIRCLE, selected = true
            ),
            TabletopLayoutSelectionUiModel(
                TabletopType.LIST, selected = false
            )
        )

        assertEquals(expectedSetOfTabletopTypes, viewModel.tabletopTypes.value?.toSet())
        assertEquals(1, viewModel.profiles.value!!.size)
        assertEquals("Default", viewModel.profiles.value?.get(0)?.name)
        assertEquals(3, viewModel.setupPlayers.value!!.size)
        assertEquals("Default", viewModel.setupPlayers.value?.get(0)?.profile?.name)
        assertEquals(2, viewModel.setupPlayers.value?.get(0)?.profile?.counters?.size)
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
    fun select_number_of_players_higher() {
        val existingColors = viewModel.setupPlayers.value?.map {
            it.color
        }?.toSet()?.toList() ?: emptyList()
        //Since this becomes a set, this proves they all have unique colors
        assertEquals(3, existingColors.size)

        viewModel.setNumberOfPlayers(4)
        assertEquals(4, viewModel.numberOfPlayers.value)
        assertEquals(4, viewModel.setupPlayers.value!!.size)
        assertEquals("Default", viewModel.setupPlayers.value?.get(3)?.profile?.name)
        val newColors = viewModel.setupPlayers.value?.map {
            it.color
        }?.toSet()?.toList() ?: emptyList()
        assertEquals(4, newColors.size)

        //New colors should have all the old colors
        assertTrue(newColors.containsAll(existingColors))
        //New colors are in the same order as old colors
        assertEquals(newColors.subList(0, 3), existingColors)
    }

    @Test
    fun select_number_of_players_lower() {
        viewModel.setNumberOfPlayers(8)
        val existingColors = viewModel.setupPlayers.value?.map {
            it.color
        }?.toSet()?.toList() ?: emptyList()
        //Since this becomes a set, this proves they all have unique colors
        assertEquals(8, existingColors.size)

        viewModel.setNumberOfPlayers(7)
        assertEquals(7, viewModel.numberOfPlayers.value)
        assertEquals(7, viewModel.setupPlayers.value!!.size)
        assertEquals("Default", viewModel.setupPlayers.value?.get(6)?.profile?.name)
        val newColors = viewModel.setupPlayers.value?.map {
            it.color
        }?.toSet()?.toList() ?: emptyList()
        assertEquals(7, newColors.size)

        //New colors should be a subset of the old color
        assertTrue(existingColors.containsAll(newColors))
        //New colors are in the same order as old colors
        assertEquals(newColors, existingColors.subList(0, 7))
    }

    @Test
    fun select_number_of_players_maintains_compatible_tabletop_type() {
        viewModel.setTabletopType(TabletopType.LIST)
        assertEquals(TabletopType.LIST, viewModel.selectedTabletopType)
        viewModel.setNumberOfPlayers(5)
        assertEquals(TabletopType.LIST, viewModel.selectedTabletopType)
    }

    @Test
    fun select_number_of_players_changes_tabletop_type_when_incompatible() {
        viewModel.setTabletopType(TabletopType.THREE_ACROSS)
        assertEquals(TabletopType.THREE_ACROSS, viewModel.selectedTabletopType)
        viewModel.setNumberOfPlayers(5)
        assertEquals(TabletopType.FIVE_CIRCLE, viewModel.selectedTabletopType)
    }

    @Test
    fun select_tabletop_type() {
        viewModel.setTabletopType(TabletopType.LIST)
        assertEquals(TabletopType.LIST, viewModel.selectedTabletopType)
    }

    @Test
    fun show_customize_button_when_tabletop_selected() {
        viewModel.setTabletopType(TabletopType.LIST)
        assertEquals(true, viewModel.showCustomizeLayoutButton.value)
    }

    @Test
    fun show_customize_false_button_when_tabletop_not_selected() {
        viewModel.setTabletopType(TabletopType.NONE)
        assertEquals(false, viewModel.showCustomizeLayoutButton.value)
    }

    @Test
    fun update_player_changes_to_unused_color() {
        val oldPlayers = viewModel.setupPlayers.value!!
        val allOldColors = oldPlayers.map { it.color }.toSet()
        assertEquals(3, allOldColors.size)
        val unusedColor = PlayerColor.allColors().find { !allOldColors.contains(it) }!!

        viewModel.updatePlayer(oldPlayers[1].copy(color = unusedColor))

        val newPlayers = viewModel.setupPlayers.value!!
        val allNewColors = newPlayers.map { it.color }.toSet()
        assertEquals(3, allNewColors.size)
        assertEquals(3, newPlayers.size)

        assertEquals(oldPlayers[0], newPlayers[0])
        assertEquals(oldPlayers[1].profile, newPlayers[1].profile)
        assertFalse(oldPlayers[1].color.equals(newPlayers[1]))
        assertEquals(oldPlayers[2], newPlayers[2])
    }

    @Test
    fun update_player_changes_to_existing_color_swaps_color() {
        val oldPlayers = viewModel.setupPlayers.value!!
        val allOldColors = oldPlayers.map { it.color }.toSet()
        assertEquals(3, allOldColors.size)
        val oldPlayer0Color = oldPlayers[0].color
        val oldPlayer2Color = oldPlayers[2].color

        viewModel.updatePlayer(oldPlayers[2].copy(color = oldPlayer0Color))

        val newPlayers = viewModel.setupPlayers.value!!
        val allNewColors = newPlayers.map { it.color }.toSet()
        assertEquals(3, allNewColors.size)
        assertEquals(3, newPlayers.size)

        assertEquals(oldPlayer2Color, newPlayers[0].color)
        assertEquals(oldPlayers[0].id, newPlayers[0].id)
        assertEquals(oldPlayers[0].profile, newPlayers[0].profile)
        assertEquals(oldPlayers[1], newPlayers[1])
        assertEquals(oldPlayer0Color, newPlayers[2].color)
        assertEquals(oldPlayers[2].id, newPlayers[2].id)
        assertEquals(oldPlayers[2].profile, newPlayers[2].profile)
    }

    @Test
    fun update_player_changes_template() {
        val oldPlayers = viewModel.setupPlayers.value!!

        viewModel.updatePlayer(oldPlayers[1].copy(profile = PlayerProfileModel("New Template")))

        val newPlayers = viewModel.setupPlayers.value!!
        assertEquals(3, newPlayers.size)
        assertEquals(oldPlayers[0], newPlayers[0])
        assertEquals(oldPlayers[1].color, newPlayers[1].color)
        assertFalse(oldPlayers[1].profile == newPlayers[1].profile)
        assertEquals(oldPlayers[2], newPlayers[2])
    }

    @Test
    fun get_setup_players_with_counters() {
        val generatedPlayers = viewModel.getSetupPlayersWithColorCounters()
        //1 counter for each other player was generated (plus 2 existing counters)
        assertEquals(4, generatedPlayers[0].profile?.counters?.size)
        assertEquals(4, generatedPlayers[1].profile?.counters?.size)
        assertEquals(4, generatedPlayers[2].profile?.counters?.size)

        val c0 = PlayerColor.allColors()[0]
        val c1 = PlayerColor.allColors()[1]
        val c2 = PlayerColor.allColors()[2]

        assertEquals(c0, generatedPlayers[0].color)
        assertEquals(c1, generatedPlayers[1].color)
        assertEquals(c2, generatedPlayers[2].color)

        assertEquals(
            listOf(
                c1,
                c2
            ),
            generatedPlayers[0].profile?.counters?.map { it.color }
                ?.filter { it != PlayerColor.NONE } ?: emptyList<PlayerColor>()
        )
        assertEquals(
            listOf(
                c0,
                c2
            ),
            generatedPlayers[1].profile?.counters?.map { it.color }
                ?.filter { it != PlayerColor.NONE } ?: emptyList<PlayerColor>()
        )
        assertEquals(
            listOf(
                c0,
                c1
            ),
            generatedPlayers[2].profile?.counters?.map { it.color }
                ?.filter { it != PlayerColor.NONE } ?: emptyList<PlayerColor>()
        )
    }

    @Test
    fun get_setup_players_with_counters_after_update() {
        viewModel.setNumberOfPlayers(2)
        viewModel.updatePlayer(
            viewModel.findSetupPlayerById(1)!!.copy(color = PlayerColor.allColors()[2])
        )
        viewModel.setNumberOfPlayers(4)
        val generatedPlayers = viewModel.getSetupPlayersWithColorCounters()


        //1 counter for each other player was generated (plus 2 existing counters)
        assertEquals(5, generatedPlayers[0].profile?.counters?.size)
        assertEquals(5, generatedPlayers[1].profile?.counters?.size)
        assertEquals(5, generatedPlayers[2].profile?.counters?.size)

        val c0 = PlayerColor.allColors()[0]
        val c1 = PlayerColor.allColors()[2] //manually set
        val c2 = PlayerColor.allColors()[1] //should use first unused color
        val c3 = PlayerColor.allColors()[3] //should use second unused color

        assertEquals(c0, generatedPlayers[0].color)
        assertEquals(c1, generatedPlayers[1].color)
        assertEquals(c2, generatedPlayers[2].color)
        assertEquals(c3, generatedPlayers[3].color)

        assertEquals(
            listOf(
                c1,
                c2,
                c3,
            ),
            generatedPlayers[0].profile?.counters?.map { it.color }
                ?.filter { it != PlayerColor.NONE } ?: emptyList<PlayerColor>()
        )
        assertEquals(
            listOf(
                c0,
                c2,
                c3,
            ),
            generatedPlayers[1].profile?.counters?.map { it.color }
                ?.filter { it != PlayerColor.NONE } ?: emptyList<PlayerColor>()
        )
        assertEquals(
            listOf(
                c0,
                c1,
                c3
            ),
            generatedPlayers[2].profile?.counters?.map { it.color }
                ?.filter { it != PlayerColor.NONE } ?: emptyList<PlayerColor>()
        )
        assertEquals(
            listOf(
                c0,
                c1,
                c2
            ),
            generatedPlayers[3].profile?.counters?.map { it.color }
                ?.filter { it != PlayerColor.NONE } ?: emptyList<PlayerColor>()
        )
    }
}