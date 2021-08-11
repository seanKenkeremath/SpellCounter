package com.kenkeremath.mtgcounter.ui.game

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.kenkeremath.mtgcounter.CoroutineTestRule
import com.kenkeremath.mtgcounter.TestApplication
import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.persistence.*
import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerTemplateEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerTemplateWithCountersEntity
import com.squareup.moshi.Moshi
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.test.TestCoroutineScope
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

    private lateinit var viewModel: GameViewModel

    private lateinit var datastore: Datastore

    @MockK
    private lateinit var database: AppDatabase

    @MockK
    private lateinit var templateDao: TemplateDao

    private lateinit var testScope: TestCoroutineScope

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        testScope = TestCoroutineScope(coroutinesTestRule.testDispatcher)
        datastore = DatastoreImpl(ApplicationProvider.getApplicationContext(), Moshi.Builder().build())
        gameRepository = GameRepositoryImpl(database, datastore, coroutinesTestRule.testDispatcherProvider)
        val counter1 = CounterTemplateEntity(
            id = 1,
            startingAmount = 3,
            name = "counter1",
            color = 1234,
            linkToPlayer = false
        )
        val counter2 = CounterTemplateEntity(
            id = 2,
            startingAmount = 4,
            name = "counter2",
            color = 7,
            linkToPlayer = true
        )
        coEvery {
            templateDao.getCounterTemplates()
        } returns listOf(
            counter1,
            counter2,
        )

        val playerTemplate = PlayerTemplateWithCountersEntity()
        playerTemplate.counters = listOf(
            counter2,
        )
        playerTemplate.template = PlayerTemplateEntity(name = "player_template")
        coEvery {
            templateDao.getPlayerTemplates()
        } returns listOf(
            playerTemplate
        )
        datastore.keepScreenOn = false
        datastore.startingLife = 15
        datastore.numberOfPlayers = 3
        datastore.tabletopType = TabletopType.THREE_CIRCLE
        viewModel = GameViewModel(gameRepository)
    }

    @Test
    fun players_created_correctly() {
        assertEquals(3, viewModel.players.value?.size)
        assertEquals(15, viewModel.players.value!![0].life)
        assertEquals(15, viewModel.players.value!![1].life)
        assertEquals(15, viewModel.players.value!![2].life)
    }

    @Test
    fun add_counter_updates_player() {
        val id = viewModel.players.value!![0].id
        assertEquals(0, viewModel.players.value!![0].counters.size)
        assertEquals(0, viewModel.players.value!![1].counters.size)
        assertEquals(0, viewModel.players.value!![2].counters.size)
        viewModel.addCounter(id)
        assertEquals(1, viewModel.players.value!![0].counters.size)
        assertEquals(0, viewModel.players.value!![1].counters.size)
        assertEquals(0, viewModel.players.value!![2].counters.size)
    }

    @Test
    fun increment_life_updates_players() {
        viewModel.incrementPlayerLife(0, 1)
        viewModel.incrementPlayerLife(1, 5)
        viewModel.incrementPlayerLife(2, 0)

        assertEquals(3, viewModel.players.value?.size)
        assertEquals(16, viewModel.players.value!![0].life)
        assertEquals(20, viewModel.players.value!![1].life)
        assertEquals(15, viewModel.players.value!![2].life)
    }

    @Test
    fun increment_life_allows_negative() {
        viewModel.incrementPlayerLife(0, -1)

        assertEquals(3, viewModel.players.value?.size)
        assertEquals(14, viewModel.players.value!![0].life)
    }

    @Test
    fun increment_life_does_nothing_when_player_id_not_found() {
        viewModel.incrementPlayerLife(-1, 1)
        viewModel.incrementPlayerLife(4, 1)

        assertEquals(3, viewModel.players.value?.size)
        assertEquals(15, viewModel.players.value!![0].life)
        assertEquals(15, viewModel.players.value!![1].life)
        assertEquals(15, viewModel.players.value!![2].life)
    }

    @Test
    fun increment_counter_updates_player() {
        val playerId = viewModel.players.value!![0].id
        viewModel.addCounter(playerId)
        assertEquals(1, viewModel.players.value!![0].counters.size)
        val counterId = viewModel.players.value!![0].counters[0].id
        assertEquals(0, viewModel.players.value!![0].counters[0].amount)

        viewModel.incrementCounter(playerId, counterId)

        assertEquals(1, viewModel.players.value!![0].counters[0].amount)

        viewModel.incrementCounter(playerId, counterId, 10)

        assertEquals(11, viewModel.players.value!![0].counters[0].amount)
    }

    @Test
    fun increment_counter_allows_negative() {
        val playerId = viewModel.players.value!![0].id
        viewModel.addCounter(playerId)
        val counterId = viewModel.players.value!![0].counters[0].id
        assertEquals(0, viewModel.players.value!![0].counters[0].amount)

        viewModel.incrementCounter(playerId, counterId, -1)

        assertEquals(-1, viewModel.players.value!![0].counters[0].amount)
    }

    @Test
    fun increment_counter_does_nothing_when_counter_id_not_found() {
        val playerId = viewModel.players.value!![0].id
        viewModel.addCounter(playerId)
        assertEquals(0, viewModel.players.value!![0].counters[0].amount)

        viewModel.incrementCounter(playerId, -9999, 1)

        assertEquals(0, viewModel.players.value!![0].counters[0].amount)
    }

    @Test
    fun increment_counter_does_nothing_when_player_id_not_found() {
        val playerId = viewModel.players.value!![0].id
        viewModel.addCounter(playerId)
        val counterId = viewModel.players.value!![0].counters[0].id

        viewModel.incrementCounter(-9999, counterId, 1)

        assertEquals(0, viewModel.players.value!![0].counters[0].amount)

    }
}