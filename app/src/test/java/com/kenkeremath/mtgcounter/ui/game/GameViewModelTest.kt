package com.kenkeremath.mtgcounter.ui.game

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.kenkeremath.mtgcounter.CoroutineTestRule
import com.kenkeremath.mtgcounter.TestApplication
import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.model.player.PlayerColor
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.model.player.PlayerSetupModel
import com.kenkeremath.mtgcounter.model.player.PlayerTemplateModel
import com.kenkeremath.mtgcounter.persistence.Datastore
import com.kenkeremath.mtgcounter.persistence.DatastoreImpl
import com.kenkeremath.mtgcounter.persistence.GameRepository
import com.kenkeremath.mtgcounter.persistence.GameRepositoryImpl
import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity
import com.squareup.moshi.Moshi
import io.mockk.MockKAnnotations
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
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

    private lateinit var testScope: TestCoroutineScope

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        testScope = TestCoroutineScope(coroutinesTestRule.testDispatcher)
        datastore =
            DatastoreImpl(ApplicationProvider.getApplicationContext(), Moshi.Builder().build())
        gameRepository = GameRepositoryImpl(datastore)

        val counterTemplate1 = CounterTemplateModel(
            id = 1,
            name = "Test1"
        )
        val counterTemplate2 = CounterTemplateModel(
            id = 2,
            name = "Test2"
        )

        val playerTemplate = PlayerTemplateModel(
            name = "player_template",
            counters = listOf(
                counterTemplate1,
                counterTemplate2
            )
        )

        savedStateHandle = SavedStateHandle(
            mapOf(
                GameActivity.ARGS_SETUP_PLAYERS to listOf(
                    PlayerSetupModel(
                        id = 0,
                        template = playerTemplate,
                        color = PlayerColor.BLUE
                    ),
                    PlayerSetupModel(
                        id = 1,
                        template = playerTemplate,
                        color = PlayerColor.RED
                    ),
                    PlayerSetupModel(
                        id = 2,
                        template = playerTemplate,
                        color = PlayerColor.GREEN
                    ),
                )
            )
        )
        val counter1 = CounterTemplateEntity(
            id = 1,
            name = "counter1",
            colorId = 1234,
            linkToPlayer = false
        )
        val counter2 = CounterTemplateEntity(
            id = 2,
            name = "counter2",
            colorId = 7,
            linkToPlayer = true
        )
        datastore.keepScreenOn = false
        datastore.startingLife = 15
        datastore.numberOfPlayers = 3
        datastore.tabletopType = TabletopType.THREE_CIRCLE
        viewModel = GameViewModel(gameRepository, savedStateHandle)
    }

    @Test
    fun players_created_correctly() = coroutinesTestRule.testDispatcher.runBlockingTest {
        assertEquals(3, viewModel.players.value?.size)
        assertEquals(15, viewModel.players.value!![0].model.life)
        assertEquals(15, viewModel.players.value!![1].model.life)
        assertEquals(15, viewModel.players.value!![2].model.life)
    }

    @Test
    fun increment_life_updates_players() = coroutinesTestRule.testDispatcher.runBlockingTest {
        viewModel.incrementPlayerLife(0, 1)
        viewModel.incrementPlayerLife(1, 5)
        viewModel.incrementPlayerLife(2, 0)

        assertEquals(3, viewModel.players.value?.size)
        assertEquals(16, viewModel.players.value!![0].model.life)
        assertEquals(20, viewModel.players.value!![1].model.life)
        assertEquals(15, viewModel.players.value!![2].model.life)
    }

    @Test
    fun increment_life_allows_negative() = coroutinesTestRule.testDispatcher.runBlockingTest {
        viewModel.incrementPlayerLife(0, -1)

        assertEquals(3, viewModel.players.value?.size)
        assertEquals(14, viewModel.players.value!![0].model.life)
    }

    @Test
    fun increment_life_does_nothing_when_player_id_not_found() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            viewModel.incrementPlayerLife(-1, 1)
            viewModel.incrementPlayerLife(4, 1)

            assertEquals(3, viewModel.players.value?.size)
            assertEquals(15, viewModel.players.value!![0].model.life)
            assertEquals(15, viewModel.players.value!![1].model.life)
            assertEquals(15, viewModel.players.value!![2].model.life)
        }

    @Test
    fun increment_counter_updates_player() = coroutinesTestRule.testDispatcher.runBlockingTest {
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
    fun increment_counter_allows_negative() = coroutinesTestRule.testDispatcher.runBlockingTest {
        val playerId = viewModel.players.value!![0].model.id
        viewModel.selectCounter(playerId, 1)
        viewModel.confirmCounterChanges(playerId)
        val counterId = viewModel.players.value!![0].model.counters[0].template.id
        assertEquals(0, viewModel.players.value!![0].model.counters[0].amount)

        viewModel.incrementCounter(playerId, counterId, -1)

        assertEquals(-1, viewModel.players.value!![0].model.counters[0].amount)
    }

    @Test
    fun increment_counter_does_nothing_when_counter_id_not_found() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            val playerId = viewModel.players.value!![0].model.id
            viewModel.selectCounter(playerId, 1)
            viewModel.confirmCounterChanges(playerId)
            assertEquals(0, viewModel.players.value!![0].model.counters[0].amount)

            viewModel.incrementCounter(playerId, -9999, 1)

            assertEquals(0, viewModel.players.value!![0].model.counters[0].amount)
        }

    @Test
    fun increment_counter_does_nothing_when_player_id_not_found() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            val playerId = viewModel.players.value!![0].model.id
            viewModel.selectCounter(playerId, 1)
            viewModel.confirmCounterChanges(playerId)
            val counterId = viewModel.players.value!![0].model.counters[0].template.id

            viewModel.incrementCounter(-9999, counterId, 1)

            assertEquals(0, viewModel.players.value!![0].model.counters[0].amount)

        }
}