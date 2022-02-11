package com.kenkeremath.mtgcounter.persistence

import androidx.room.*
import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerProfileCounterTemplateCrossRefEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerProfileEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerProfileWithCountersEntity

@Dao
abstract class TemplateDao {

    @Transaction
    @Query("Select * from ${PlayerProfileEntity.TABLE_PLAYER_PROFILES}")
    abstract fun getPlayerProfiles() : List<PlayerProfileWithCountersEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(playerProfileEntity: PlayerProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertPlayers(playerProfileEntities: List<PlayerProfileEntity>)

    @Query("Select * from ${CounterTemplateEntity.TABLE_COUNTER_TEMPLATES}")
    abstract fun getCounterTemplates() : List<CounterTemplateEntity>

    //Returns generated id
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(counterTemplateEntity: CounterTemplateEntity): Long

    //Returns generated ids
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCounters(counterTemplateEntities: List<CounterTemplateEntity>): Array<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(pairing: PlayerProfileCounterTemplateCrossRefEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertPlayerCounterPairings(pairings: List<PlayerProfileCounterTemplateCrossRefEntity>)

    @Query("DELETE FROM ${PlayerProfileCounterTemplateCrossRefEntity.TABLE_PLAYER_COUNTER_CROSS_REFS} WHERE ${PlayerProfileCounterTemplateCrossRefEntity.COLUMN_PLAYER_PROFILE_ID} = :playerProfileName")
    abstract suspend fun deletePlayerCounterCrossRefsForPlayerProfile(playerProfileName: String)

    @Query("DELETE FROM ${PlayerProfileCounterTemplateCrossRefEntity.TABLE_PLAYER_COUNTER_CROSS_REFS} WHERE ${PlayerProfileCounterTemplateCrossRefEntity.COLUMN_COUNTER_TEMPLATE_ID} = :counterTemplateId")
    abstract suspend fun deletePlayerCounterCrossRefsForCounter(counterTemplateId: Int)

    @Query("DELETE FROM ${PlayerProfileEntity.TABLE_PLAYER_PROFILES} WHERE ${PlayerProfileEntity.COLUMN_PLAYER_PROFILE_NAME} = :playerProfileName")
    abstract suspend fun deletePlayerProfileFromPlayerTable(playerProfileName: String)

    @Query("DELETE FROM ${CounterTemplateEntity.TABLE_COUNTER_TEMPLATES} WHERE ${CounterTemplateEntity.COLUMN_COUNTER_TEMPLATE_ID} = :counterTemplateId")
    abstract suspend fun deleteCounterTemplateFromCounterTable(counterTemplateId: Int)

    @Transaction
    open suspend fun deletePlayerProfile(playerProfileName: String) {
        deletePlayerProfileFromPlayerTable(playerProfileName)
        deletePlayerCounterCrossRefsForPlayerProfile(playerProfileName)
    }

    @Transaction
    open suspend fun deleteCounterTemplate(counterTemplateId: Int) {
        deleteCounterTemplateFromCounterTable(counterTemplateId)
        deletePlayerCounterCrossRefsForCounter(counterTemplateId)
    }

    @Transaction
    open suspend fun replacePlayerProfile(playerProfile: PlayerProfileEntity) {
        deletePlayerProfile(playerProfile.name)
        insert(playerProfile)
    }
}