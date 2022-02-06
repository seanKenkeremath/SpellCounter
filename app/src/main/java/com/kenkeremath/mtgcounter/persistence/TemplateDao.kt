package com.kenkeremath.mtgcounter.persistence

import androidx.room.*
import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerCounterTemplateCrossRefEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerTemplateEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerTemplateWithCountersEntity

@Dao
abstract class TemplateDao {

    @Transaction
    @Query("Select * from ${PlayerTemplateEntity.TABLE_PLAYER_TEMPLATES}")
    abstract fun getPlayerTemplates() : List<PlayerTemplateWithCountersEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(playerTemplateEntity: PlayerTemplateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertPlayers(playerTemplateEntities: List<PlayerTemplateEntity>)

    @Query("Select * from ${CounterTemplateEntity.TABLE_COUNTER_TEMPLATES}")
    abstract fun getCounterTemplates() : List<CounterTemplateEntity>

    //Returns generated id
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(counterTemplateEntity: CounterTemplateEntity): Long

    //Returns generated ids
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCounters(counterTemplateEntities: List<CounterTemplateEntity>): Array<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(pairing: PlayerCounterTemplateCrossRefEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertPlayerCounterPairings(pairings: List<PlayerCounterTemplateCrossRefEntity>)

    @Query("DELETE FROM ${PlayerCounterTemplateCrossRefEntity.TABLE_PLAYER_COUNTER_CROSS_REFS} WHERE ${PlayerCounterTemplateCrossRefEntity.COLUMN_PLAYER_TEMPLATE_ID} = :playerTemplateName")
    abstract suspend fun deletePlayerCounterCrossRefsForPlayerTemplate(playerTemplateName: String)

    @Query("DELETE FROM ${PlayerCounterTemplateCrossRefEntity.TABLE_PLAYER_COUNTER_CROSS_REFS} WHERE ${PlayerCounterTemplateCrossRefEntity.COLUMN_COUNTER_TEMPLATE_ID} = :counterTemplateId")
    abstract suspend fun deletePlayerCounterCrossRefsForCounter(counterTemplateId: Int)

    @Query("DELETE FROM ${PlayerTemplateEntity.TABLE_PLAYER_TEMPLATES} WHERE ${PlayerTemplateEntity.COLUMN_PLAYER_TEMPLATE_NAME} = :playerTemplateName")
    abstract suspend fun deletePlayerTemplateFromPlayerTable(playerTemplateName: String)

    @Query("DELETE FROM ${CounterTemplateEntity.TABLE_COUNTER_TEMPLATES} WHERE ${CounterTemplateEntity.COLUMN_COUNTER_TEMPLATE_ID} = :counterTemplateId")
    abstract suspend fun deleteCounterTemplateFromCounterTable(counterTemplateId: Int)

    @Transaction
    open suspend fun deletePlayerTemplate(playerTemplateName: String) {
        deletePlayerTemplateFromPlayerTable(playerTemplateName)
        deletePlayerCounterCrossRefsForPlayerTemplate(playerTemplateName)
    }

    @Transaction
    open suspend fun deleteCounterTemplate(counterTemplateId: Int) {
        deleteCounterTemplateFromCounterTable(counterTemplateId)
        deletePlayerCounterCrossRefsForCounter(counterTemplateId)
    }

    @Transaction
    open suspend fun replacePlayerTemplate(playerTemplate: PlayerTemplateEntity) {
        deletePlayerTemplate(playerTemplate.name)
        insert(playerTemplate)
    }
}