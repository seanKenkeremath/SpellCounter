package com.kenkeremath.mtgcounter.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerProfileCounterTemplateCrossRefEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerProfileEntity

@Database(entities = [PlayerProfileEntity::class, CounterTemplateEntity::class, PlayerProfileCounterTemplateCrossRefEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun templateDao(): TemplateDao
}