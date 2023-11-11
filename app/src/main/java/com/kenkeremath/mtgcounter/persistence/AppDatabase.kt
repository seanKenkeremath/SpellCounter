package com.kenkeremath.mtgcounter.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerProfileCounterTemplateCrossRefEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerProfileEntity

@Database(
    entities = [PlayerProfileEntity::class, CounterTemplateEntity::class, PlayerProfileCounterTemplateCrossRefEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun templateDao(): TemplateDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE player_profiles ADD COLUMN life_counter_id INTEGER")
            }
        }
    }
}