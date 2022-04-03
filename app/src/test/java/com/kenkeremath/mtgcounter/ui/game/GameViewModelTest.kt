package com.kenkeremath.mtgcounter.ui.game

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.kenkeremath.mtgcounter.CoroutineTestRule
import com.kenkeremath.mtgcounter.TestApplication
import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.model.player.PlayerColor
import com.kenkeremath.mtgcounter.model.player.PlayerProfileModel
import com.kenkeremath.mtgcounter.model.player.PlayerSetupModel
import com.kenkeremath.mtgcounter.persistence.Datastore
import com.kenkeremath.mtgcounter.persistence.DatastoreImpl
import com.kenkeremath.mtgcounter.persistence.GameRepository
import com.kenkeremath.mtgcounter.persistence.GameRepositoryImpl
import io.mockk.MockKAnnotations
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O], application = TestApplication::class)
class GameViewModelTest {
    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private lateinit var gameRepository: GameRepository

    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: GameViewModel

    private lateinit var datastore: Datastore

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        datastore =
            DatastoreImpl(ApplicationProvider.getApplicationContext())
        gameRepository = GameRepositoryImpl(datastore)

        val counterTemplate1 = CounterTemplateModel(
            id = 1,
            name = "Test1"
        )
        val counterTemplate2 = CounterTemplateModel(
            id = 2,
            name = "Test2"
        )
        val counterTemplate3 = CounterTemplateModel(
            id = 3,
            name = "Test3"
        )
        val counterTemplate4 = CounterTemplateModel(
            id = 4,
            name = "Test4"
        )
        val counterTemplate5 = CounterTemplateModel(
            id = 5,
            name = "Test5"
        )
        val counterTemplate6 = CounterTemplateModel(
            id = 6,
            name = "Test6"
        )

        val playerTemplate = PlayerProfileModel(
            name = "player_template",
            counters = listOf(
                counterTemplate1,
                counterTemplate2,
                counterTemplate3,
                counterTemplate4,
                counterTemplate5,
                counterTemplate6,
            )
        )

        savedStateHandle = SavedStateHandle(
            mapOf(
                GameActivity.ARGS_SETUP_PLAYERS to listOf(
                    PlayerSetupModel(
                        id = 0,
                        profile = playerTemplate,
                        color = PlayerColor.BLUE
                    ),
                    PlayerSetupModel(
                        id = 1,
                        profile = playerTemplate,
                        color = PlayerColor.RED
                    ),
                    PlayerSetupModel(
                        id = 2,
                        profile = playerTemplate,
                        color = PlayerColor.GREEN
                    ),
                )
            )
        )
        datastore.keepScreenOn = false
        datastore.startingLife = 15
        datastore.numberOfPlayers = 3
        datastore.tabletopType = TabletopType.THREE_CIRCLE
        viewModel = GameViewModel(gameRepository, savedStateHandle)
    }

    @Test
    fun players_created_correctly() {
        assertEquals(3, viewModel.players.value?.size)
        assertEquals(15, viewModel.players.value!![0].model.life)
        assertEquals(15, viewModel.players.value!![1].model.life)
        assertEquals(15, viewModel.players.value!![2].model.life)
    }

    @Test
    fun increment_life_updates_players() {
        viewModel.incrementPlayerLife(0, 1)
        viewModel.incrementPlayerLife(1, 5)
        viewModel.incrementPlayerLife(2, 0)

        assertEquals(3, viewModel.players.value?.size)
        assertEquals(16, viewModel.players.value!![0].model.life)
        assertEquals(20, viewModel.players.value!![1].model.life)
        assertEquals(15, viewModel.players.value!![2].model.life)
    }

    @Test
    fun increment_life_allows_negative() {
        viewModel.incrementPlayerLife(0, -1)

        assertEquals(3, viewModel.players.value?.size)
        assertEquals(14, viewModel.players.value!![0].model.life)
    }

    @Test
    fun increment_life_does_nothing_when_player_id_not_found() {
        viewModel.incrementPlayerLife(-1, 1)
        viewModel.incrementPlayerLife(4, 1)

        assertEquals(3, viewModel.players.value?.size)
        assertEquals(15, viewModel.players.value!![0].model.life)
        assertEquals(15, viewModel.players.value!![1].model.life)
        assertEquals(15, viewModel.players.value!![2].model.life)
    }

    @Test
    fun increment_counter_updates_player() {
        val playerId = viewModel.players.value!![0].model.id
        viewModel.editCounters(playerId)
        viewModel.selectCounter(playerId, 1)
        viewModel.confirmCounterChanges(playerId)
        assertEquals(1, viewModel.players.value!![0].model.counters.size)
        val counterId = viewModel.players.value!![0].model.counters[0].template.id

        assertEquals(0, viewModel.players.value!![0].model.counters[0].amount)

        viewModel.incrementCounter(playerId, counterId)

        assertEquals(1, viewModel.players.value!![0].model.counters[0].amount)

        viewModel.incrementCounter(playerId, counterId, 10)

        assertEquals(11, viewModel.players.value!![0].model.counters[0].amount)
    }

    @Test
    fun increment_counter_allows_negative() {
        val playerId = viewModel.players.value!![0].model.id
        viewModel.editCounters(playerId)
        viewModel.selectCounter(playerId, 1)
        viewModel.confirmCounterChanges(playerId)
        val counterId = viewModel.players.value!![0].model.counters[0].template.id
        assertEquals(0, viewModel.players.value!![0].model.counters[0].amount)

        viewModel.incrementCounter(playerId, counterId, -1)

        assertEquals(-1, viewModel.players.value!![0].model.counters[0].amount)
    }

    @Test
    fun increment_counter_does_nothing_when_counter_id_not_found() {
        val playerId = viewModel.players.value!![0].model.id
        viewModel.editCounters(playerId)
        viewModel.selectCounter(playerId, 1)
        viewModel.confirmCounterChanges(playerId)
        assertEquals(0, viewModel.players.value!![0].model.counters[0].amount)

        viewModel.incrementCounter(playerId, -9999, 1)

        assertEquals(0, viewModel.players.value!![0].model.counters[0].amount)
    }

    @Test
    fun increment_counter_does_nothing_when_player_id_not_found() {
        val playerId = viewModel.players.value!![0].model.id
        viewModel.editCounters(playerId)
        viewModel.selectCounter(playerId, 1)
        viewModel.confirmCounterChanges(playerId)
        val counterId = viewModel.players.value!![0].model.counters[0].template.id

        viewModel.incrementCounter(-9999, counterId, 1)

        assertEquals(0, viewModel.players.value!![0].model.counters[0].amount)

    }

    @Test
    fun player_models_created_from_setup() {
        val players = viewModel.players.value!!
        assertEquals(3, players.size)

        assertEquals(0, players[0].model.id)
        assertEquals(PlayerColor.BLUE.resId, players[0].model.colorResId)
        assertEquals(0, players[0].model.counters.size)
        assertEquals(15, players[0].model.life)
        assertEquals(false, players[0].rearrangeButtonEnabled)
        assertEquals(true, players[0].pullToReveal)
        assertEquals(6, players[0].counterSelections.size)
        assertEquals(0, players[0].rearrangeCounters.size)
        assertEquals(false, players[0].newCounterAdded)
        assertEquals(GamePlayerUiModel.Menu.MAIN, players[0].currentMenu)

        assertEquals(1, players[1].model.id)
        assertEquals(PlayerColor.RED.resId, players[1].model.colorResId)
        assertEquals(0, players[1].model.counters.size)
        assertEquals(15, players[1].model.life)
        assertEquals(false, players[1].rearrangeButtonEnabled)
        assertEquals(true, players[1].pullToReveal)
        assertEquals(6, players[1].counterSelections.size)
        assertEquals(0, players[1].rearrangeCounters.size)
        assertEquals(false, players[1].newCounterAdded)
        assertEquals(GamePlayerUiModel.Menu.MAIN, players[1].currentMenu)

        assertEquals(2, players[2].model.id)
        assertEquals(PlayerColor.GREEN.resId, players[2].model.colorResId)
        assertEquals(0, players[2].model.counters.size)
        assertEquals(15, players[2].model.life)
        assertEquals(false, players[2].rearrangeButtonEnabled)
        assertEquals(true, players[2].pullToReveal)
        assertEquals(6, players[2].counterSelections.size)
        assertEquals(0, players[2].rearrangeCounters.size)
        assertEquals(false, players[2].newCounterAdded)
        assertEquals(GamePlayerUiModel.Menu.MAIN, players[2].currentMenu)

    }

    @Test
    fun player_opens_edit_menu_and_closes_without_saving() {
        val id0 = viewModel.players.value!![0].model.id
        viewModel.editCounters(id0)

        var players = viewModel.players.value!!

        assertEquals(GamePlayerUiModel.Menu.EDIT_COUNTERS, players[0].currentMenu)
        assertEquals(GamePlayerUiModel.Menu.MAIN, players[1].currentMenu)
        assertEquals(GamePlayerUiModel.Menu.MAIN, players[2].currentMenu)

        viewModel.selectCounter(id0, 1)
        viewModel.selectCounter(id0, 2)
        viewModel.closeSubMenu(id0)

        players = viewModel.players.value!!

        assertEquals(GamePlayerUiModel.Menu.MAIN, players[0].currentMenu)
        assertEquals(GamePlayerUiModel.Menu.MAIN, players[1].currentMenu)
        assertEquals(GamePlayerUiModel.Menu.MAIN, players[2].currentMenu)

        assertEquals(6, players[0].counterSelections.size)
        assertEquals(0, players[0].model.counters.size)
        assertEquals(0, players[0].rearrangeCounters.size)
    }

    @Test
    fun player_opens_edit_menu_and_saves_changes() {
        val id0 = viewModel.players.value!![0].model.id
        viewModel.editCounters(id0)

        var players = viewModel.players.value!!

        assertEquals(GamePlayerUiModel.Menu.EDIT_COUNTERS, players[0].currentMenu)
        assertEquals(GamePlayerUiModel.Menu.MAIN, players[1].currentMenu)
        assertEquals(GamePlayerUiModel.Menu.MAIN, players[2].currentMenu)

        viewModel.selectCounter(id0, 1)
        viewModel.selectCounter(id0, 2)
        viewModel.confirmCounterChanges(id0)

        players = viewModel.players.value!!

        assertEquals(GamePlayerUiModel.Menu.MAIN, players[0].currentMenu)
        assertEquals(GamePlayerUiModel.Menu.MAIN, players[1].currentMenu)
        assertEquals(GamePlayerUiModel.Menu.MAIN, players[2].currentMenu)

        assertEquals(6, players[0].counterSelections.size)
        assertEquals(2, players[0].model.counters.size)
        assertEquals(2, players[0].rearrangeCounters.size)
        assertEquals("Test1", players[0].model.counters[0].template.name)
        assertEquals("Test2", players[0].model.counters[1].template.name)
        assertEquals("Test1", players[0].rearrangeCounters[0].template.name)
        assertEquals("Test2", players[0].rearrangeCounters[1].template.name)

        assertEquals(0, players[1].model.counters.size)
        assertEquals(0, players[1].rearrangeCounters.size)

        assertEquals(0, players[2].model.counters.size)
        assertEquals(0, players[2].rearrangeCounters.size)
    }

    @Test
    fun player_opens_rearrange_menu_and_closes_without_saving() {
        val id0 = viewModel.players.value!![0].model.id
        viewModel.editCounters(id0)
        viewModel.selectCounter(id0, 1)
        viewModel.selectCounter(id0, 2)
        viewModel.confirmCounterChanges(id0)
        viewModel.rearrangeCounters(id0)

        var players = viewModel.players.value!!
        assertEquals(GamePlayerUiModel.Menu.REARRANGE_COUNTERS, players[0].currentMenu)

        viewModel.moveCounter(id0, 0, 1)
        viewModel.closeSubMenu(id0)

        players = viewModel.players.value!!
        assertEquals(GamePlayerUiModel.Menu.MAIN, players[0].currentMenu)
        assertEquals(6, players[0].counterSelections.size)
        assertEquals(2, players[0].model.counters.size)
        assertEquals(2, players[0].rearrangeCounters.size)
        assertEquals("Test1", players[0].model.counters[0].template.name)
        assertEquals("Test2", players[0].model.counters[1].template.name)
        assertEquals("Test1", players[0].rearrangeCounters[0].template.name)
        assertEquals("Test2", players[0].rearrangeCounters[1].template.name)
    }

    @Test
    fun player_opens_rearrange_menu_and_saves_changes() {
        val id0 = viewModel.players.value!![0].model.id
        viewModel.editCounters(id0)
        viewModel.selectCounter(id0, 1)
        viewModel.selectCounter(id0, 2)
        viewModel.selectCounter(id0, 3)
        viewModel.selectCounter(id0, 4)
        viewModel.confirmCounterChanges(id0)
        viewModel.rearrangeCounters(id0) //1, 2, 3, 4
        viewModel.moveCounter(id0, 0, 3) //2, 3, 4, 1
        viewModel.moveCounter(id0,  3, 2) //2, 3, 1, 4
        //This should do nothing but not crash since outside bounds
        viewModel.moveCounter(id0,  7, 2) //2, 3, 1, 4
        //This should do nothing but not crash since outside bounds
        viewModel.moveCounter(id0,  2, 7) //2, 3, 1, 4
        viewModel.moveCounter(id0,  3, 0) //4, 2, 3, 1
        viewModel.confirmCounterChanges(id0)


        val players = viewModel.players.value!!
        assertEquals(GamePlayerUiModel.Menu.MAIN, players[0].currentMenu)
        assertEquals(6, players[0].counterSelections.size)
        assertEquals(4, players[0].model.counters.size)
        assertEquals(4, players[0].rearrangeCounters.size)
        assertEquals("Test4", players[0].model.counters[0].template.name)
        assertEquals("Test2", players[0].model.counters[1].template.name)
        assertEquals("Test3", players[0].model.counters[2].template.name)
        assertEquals("Test1", players[0].model.counters[3].template.name)
        assertEquals("Test4", players[0].rearrangeCounters[0].template.name)
        assertEquals("Test2", players[0].rearrangeCounters[1].template.name)
        assertEquals("Test3", players[0].rearrangeCounters[2].template.name)
        assertEquals("Test1", players[0].rearrangeCounters[3].template.name)
    }
}