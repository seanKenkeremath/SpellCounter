package com.kenkeremath.mtgcounter.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kenkeremath.mtgcounter.model.counter.CounterSymbol
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.model.player.PlayerProfileModel
import com.kenkeremath.mtgcounter.persistence.AppDatabase
import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerProfileCounterTemplateCrossRefEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerProfileEntity
import kotlinx.coroutines.runBlocking

object MockDatabaseFactory {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private fun createDatabase(): AppDatabase {
        return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    fun createEmptyDatabase(): AppDatabase {
        return createDatabase()
    }

    fun createDefaultTemplateDatabase(): AppDatabase {
        val db = createDatabase()
        val defaultProfile = PlayerProfileEntity(PlayerProfileModel.NAME_DEFAULT, false)
        val counter1 = CounterTemplateModel(
            name = "XP",
        )
        val counter2 = CounterTemplateModel(
            symbol = CounterSymbol.SWAMP,
        )
        runBlocking {
            val counters = db.templateDao().insertCounters(
                listOf(
                    CounterTemplateEntity(
                        counter1
                    ),
                    CounterTemplateEntity(
                        counter2
                    )
                )
            )
            val defaultProfileLinks = listOf(
                PlayerProfileCounterTemplateCrossRefEntity(
                    defaultProfile.name,
                    counters[0].toInt()
                ),
                PlayerProfileCounterTemplateCrossRefEntity(
                    defaultProfile.name,
                    counters[1].toInt()
                ),
            )
            db.templateDao().insert(PlayerProfileEntity(defaultProfile.name, false))
            db.templateDao().insertPlayerCounterPairings(defaultProfileLinks)
        }
        return db
    }
}