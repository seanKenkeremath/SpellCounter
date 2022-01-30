package com.kenkeremath.mtgcounter.persistence

import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.model.player.PlayerTemplateModel
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getAllPlayerTemplates(): Flow<List<PlayerTemplateModel>>
    fun getPlayerTemplateByName(profileName: String): Flow<PlayerTemplateModel?>
    fun getAllCounters(): Flow<List<CounterTemplateModel>>
    fun getAllCountersForProfile(profileName: String): Flow<List<CounterTemplateModel>>
    fun addPlayerTemplate(playerTemplate: PlayerTemplateModel): Flow<Boolean>
    fun addCounterTemplate(counterTemplate: CounterTemplateModel): Flow<Boolean>
    fun deletePlayerTemplate(profileName: String): Flow<Boolean>
    fun createStockTemplates(): Flow<Boolean>
    fun preloadCache(): Flow<Boolean>
}