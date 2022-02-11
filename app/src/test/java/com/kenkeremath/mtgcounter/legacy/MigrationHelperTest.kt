package com.kenkeremath.mtgcounter.legacy

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.kenkeremath.mtgcounter.CoroutineTestRule
import com.kenkeremath.mtgcounter.TestApplication
import com.kenkeremath.mtgcounter.data.MockDatabaseFactory
import com.kenkeremath.mtgcounter.legacy.model.LegacyCounterTemplateModel
import com.kenkeremath.mtgcounter.legacy.model.LegacyPlayerTemplateModel
import com.kenkeremath.mtgcounter.model.counter.CounterSymbol
import com.kenkeremath.mtgcounter.model.player.PlayerProfileModel
import com.kenkeremath.mtgcounter.persistence.*
import com.squareup.moshi.Moshi
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O], application = TestApplication::class)
class MigrationHelperTest {
    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private lateinit var database: AppDatabase
    private lateinit var datastore: DatastoreImpl
    private lateinit var migrationHelper: MigrationHelper

    @Before
    fun setup() {
        datastore =
            DatastoreImpl(ApplicationProvider.getApplicationContext(), Moshi.Builder().build())
        database = MockDatabaseFactory.createEmptyDatabase()
        migrationHelper =
            MigrationHelper(datastore, database, coroutinesTestRule.testDispatcherProvider)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun migration_from_fresh_install_creates_stock_templates() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            val oldProfiles = database.templateDao().getPlayerProfiles()
            assertEquals(0, datastore.version)

            migrationHelper.performMigration().collect {}

            val newProfiles = database.templateDao().getPlayerProfiles()
            assertEquals(Datastore.CURRENT_VERSION, datastore.version)
            assertEquals(0, oldProfiles.size)
            assertEquals(1, newProfiles.size)
            assertEquals(PlayerProfileModel.NAME_DEFAULT, newProfiles[0].profile.name)
            val counters = newProfiles[0].counters
            //CounterSymbol.NONE does not count, but we have an additional XP test stock template
            assertEquals(CounterSymbol.values().size, counters.size)
            assertTrue(counters.find { it.symbolId == CounterSymbol.FOREST.symbolId } != null)
            assertTrue(counters.find { it.symbolId == CounterSymbol.MOUNTAIN.symbolId } != null)
            assertTrue(counters.find { it.name == "XP" } != null)
        }

    @Test
    fun migration_from_legacy_imports_templates_and_creates_stock() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            val legacyTemplates = listOf(
                LegacyPlayerTemplateModel(
                    templateName = "Test_player1",
                    startingLife = 15,
                    counters = listOf(
                        LegacyCounterTemplateModel("C1", 22),
                        //Should still be included since this is a custom profile
                        LegacyCounterTemplateModel("STRM", 0),
                        LegacyCounterTemplateModel("C3", 22),
                    )
                ),
                //This one should be skipped, but any custom counters should be imported
                LegacyPlayerTemplateModel(
                    templateName = "Modern",
                    counters = listOf(
                        //These should not be brought in
                        LegacyCounterTemplateModel("CMD", 0),
                        LegacyCounterTemplateModel("MANA", 0),
                        //This one is custom so it should be imported
                        LegacyCounterTemplateModel("C4", 0),
                    )
                ),
                LegacyPlayerTemplateModel(
                    templateName = "Test_player2",
                    startingLife = 25,
                    counters = listOf(
                        LegacyCounterTemplateModel("C5", 22),
                        LegacyCounterTemplateModel("C6", 22),
                    )
                )
            )
            datastore.setLegacyTemplates(legacyTemplates)
            assertEquals(0, datastore.version)

            migrationHelper.performMigration().collect {}

            val newProfiles = database.templateDao().getPlayerProfiles()
            assertEquals(Datastore.CURRENT_VERSION, datastore.version)

            /**
             * Assert Profiles are imported successfully
             */
            //Default, and both test players (not "Modern")
            assertEquals(3, newProfiles.size)
            val profilesMap = newProfiles.associateBy {
                it.profile.name
            }
            assertEquals(
                setOf(
                    PlayerProfileModel.NAME_DEFAULT,
                    "Test_player1",
                    "Test_player2"
                ), profilesMap.keys
            )

            val defaultProfile = profilesMap[PlayerProfileModel.NAME_DEFAULT]!!
            val testProfile1 = profilesMap["Test_player1"]!!
            val testProfile2 = profilesMap["Test_player2"]!!

            //CounterSymbol.NONE does not count, but we have an additional XP test stock template
            assertEquals(CounterSymbol.values().size, defaultProfile.counters.size)
            assertEquals(false, defaultProfile.profile.deletable)
            for (counter in defaultProfile.counters) {
                if (counter.name == "XP") {
                    assertEquals(true, counter.deletable)
                } else {
                    assertEquals(false, counter.deletable)
                }
            }

            assertEquals(3, testProfile1.counters.size)
            assertEquals(true, testProfile1.profile.deletable)
            val profile1CounterNames = testProfile1.counters.associateBy { it.name }
            assertEquals(
                setOf(
                    "C1",
                    "STRM",
                    "C3"
                ),
                profile1CounterNames.keys
            )
            for (counter in testProfile1.counters) {
                assertEquals(true, counter.deletable)
            }

            assertEquals(2, testProfile2.counters.size)
            assertEquals(true, testProfile2.profile.deletable)
            val profile2CounterNames = testProfile2.counters.associateBy { it.name }
            assertEquals(
                setOf(
                    "C5",
                    "C6"
                ),
                profile2CounterNames.keys
            )
            for (counter in testProfile2.counters) {
                assertEquals(true, counter.deletable)
            }

            /**
             * Assert additional custom counters imported successfully
             */
            val allCounters = database.templateDao().getCounterTemplates()

            //There should be 1 extra which is the custom counter added to the "Modern" template
            val expectedCounterSize =
                defaultProfile.counters.size + testProfile1.counters.size + testProfile2.counters.size + 1
            assertEquals(expectedCounterSize, allCounters.size)
            assertTrue(allCounters.find {it.name == "C4"} != null)
            assertTrue(allCounters.find {it.name == "CMD"} == null)
            assertTrue(allCounters.find {it.name == "MANA"} == null)
            assertTrue(allCounters.find {it.name == "C4"}?.deletable == true)
        }
}