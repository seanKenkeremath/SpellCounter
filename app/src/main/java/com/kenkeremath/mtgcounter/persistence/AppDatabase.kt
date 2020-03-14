package com.kenkeremath.mtgcounter.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerCounterTemplateCrossRefEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerTemplateEntity

@Database(entities = [PlayerTemplateEntity::class, CounterTemplateEntity::class, PlayerCounterTemplateCrossRefEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun templateDao(): TemplateDao
}