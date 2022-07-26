package com.kenkeremath.mtgcounter.persistence

import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.model.player.PlayerProfileModel
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getAllPlayerProfiles(): Flow<List<PlayerProfileModel>>
    fun getPlayerProfileByName(profileName: String): Flow<PlayerProfileModel?>
    fun getAllCounters(): Flow<List<CounterTemplateModel>>
    fun getAllCountersForProfile(profileName: String): Flow<List<CounterTemplateModel>>
    fun addPlayerProfile(playerProfile: PlayerProfileModel): Flow<Boolean>
    fun deletePlayerProfile(profileName: String): Flow<Boolean>
    fun addCounterTemplate(counterTemplate: CounterTemplateModel): Flow<Int>
    fun addCounterTemplates(counterTemplates: List<CounterTemplateModel>): Flow<List<Int>>
    fun addCounterTemplateToProfile(counterTemplate: CounterTemplateModel, profileName: String): Flow<Int>
    fun deleteCounterTemplate(counterId: Int): Flow<Boolean>
    fun preloadCache(): Flow<Boolean>
}