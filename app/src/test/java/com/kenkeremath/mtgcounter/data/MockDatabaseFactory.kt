package com.kenkeremath.mtgcounter.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kenkeremath.mtgcounter.model.counter.CounterSymbol
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.model.player.PlayerTemplateModel
import com.kenkeremath.mtgcounter.persistence.AppDatabase
import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerCounterTemplateCrossRefEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerTemplateEntity
import kotlinx.coroutines.runBlocking

object MockDatabaseFactory {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private fun createDatabase(): AppDatabase {
        return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    fun createDefaultTemplateDatabase(): AppDatabase {
        val db = createDatabase()
        val defaultProfile = PlayerTemplateEntity(PlayerTemplateModel.NAME_DEFAULT, false)
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
                PlayerCounterTemplateCrossRefEntity(
                    defaultProfile.name,
                    counters[0].toInt()
                ),
                PlayerCounterTemplateCrossRefEntity(
                    defaultProfile.name,
                    counters[1].toInt()
                ),
            )
            db.templateDao().insert(PlayerTemplateEntity(defaultProfile.name, false))
            db.templateDao().insertPlayerCounterPairings(defaultProfileLinks)
        }
        return db
    }
}