package com.kenkeremath.mtgcounter.persistence

import com.kenkeremath.mtgcounter.coroutines.DefaultDispatcherProvider
import com.kenkeremath.mtgcounter.coroutines.DispatcherProvider
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.model.player.PlayerProfileModel
import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerProfileCounterTemplateCrossRefEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerProfileEntity
import kotlinx.coroutines.flow.*
import javax.inject.Inject


class ProfileRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val datastore: Datastore,
    private val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider()
) : ProfileRepository {

    private var cachedPlayerProfiles: List<PlayerProfileModel>? = null
    private var cachedCounterTemplates: List<CounterTemplateModel>? = null

    override fun getAllPlayerProfiles(): Flow<List<PlayerProfileModel>> {
        return cachedPlayerProfiles?.let {
            flowOf(it)
        } ?: flow {
            val entities = database.templateDao().getPlayerProfiles()
            val templates = entities.map {
                PlayerProfileModel(it)
            }
            cachedPlayerProfiles = templates
            emit(templates)
        }.flowOn(dispatcherProvider.default())
    }

    override fun getPlayerProfileByName(profileName: String): Flow<PlayerProfileModel?> {
        return getAllPlayerProfiles().map { profiles ->
            profiles.find { it.name == profileName }
        }
    }

    override fun getAllCounters(): Flow<List<CounterTemplateModel>> {
        return cachedCounterTemplates?.let {
            flowOf(it)
        } ?: flow {
            val entities = database.templateDao().getCounterTemplates()
            val templates = entities.map {
                CounterTemplateModel(it)
            }
            cachedCounterTemplates = templates
            emit(templates)
        }.flowOn(dispatcherProvider.default())
    }

    override fun getAllCountersForProfile(profileName: String): Flow<List<CounterTemplateModel>> {
        return getPlayerProfileByName(profileName).map {
            it?.counters ?: emptyList()
        }
    }

    override fun addPlayerProfile(playerProfile: PlayerProfileModel): Flow<Boolean> {
        invalidateCache()
        return flow {
            addPlayerProfileInternal(playerProfile)
            emit(true)
        }
            .flatMapConcat { preloadCache() }
            .flowOn(dispatcherProvider.default())
    }

    private suspend fun addPlayerProfileInternal(
        playerProfile: PlayerProfileModel
    ) {
        val playerEntity =
            PlayerProfileEntity(
                name = playerProfile.name,
                deletable = playerProfile.deletable
            )
        database.templateDao().deletePlayerCounterCrossRefsForPlayerProfile(playerProfile.name)
        database.templateDao().replacePlayerProfile(playerEntity)
        val counterIds = addCounterTemplatesInternal(playerProfile.counters)
        database.templateDao().insertPlayerCounterPairings(
            counterIds.map {
                PlayerProfileCounterTemplateCrossRefEntity(
                    playerProfileId = playerEntity.name,
                    counterTemplateId = it,
                )
            }
        )
    }

    override fun addCounterTemplate(counterTemplate: CounterTemplateModel): Flow<Int> {
        invalidateCache()
        return flow {
            val id = addCounterTemplateInternal(counterTemplate)
            emit(id)
        }
            .flatMapConcat { counterId ->
                preloadCache().map { counterId }
            }
            .flowOn(dispatcherProvider.default())
    }

    override fun addCounterTemplates(counterTemplates: List<CounterTemplateModel>): Flow<List<Int>> {
        invalidateCache()
        return flow {
            val results = addCounterTemplatesInternal(counterTemplates)
            emit(results)
        }
            .flatMapConcat { counterIds ->
                preloadCache().map { counterIds }
            }
            .flowOn(dispatcherProvider.default())
    }

    override fun addCounterTemplateToProfile(
        counterTemplate: CounterTemplateModel,
        profileName: String
    ): Flow<Int> {
        invalidateCache()
        return flow {
            val id = addCounterTemplateInternal(counterTemplate)
            database.templateDao().insert(
                PlayerProfileCounterTemplateCrossRefEntity(
                    playerProfileId = profileName,
                    counterTemplateId = id
                )
            )
            emit(id)
        }
            .flatMapConcat { counterId ->
                preloadCache().map { counterId }
            }
            .flowOn(dispatcherProvider.default())
    }

    override fun deleteCounterTemplate(counterId: Int): Flow<Boolean> {
        invalidateCache()
        return flow {
            database.templateDao().deleteCounterTemplate(counterId)
            emit(true)
        }
            .flatMapConcat { counterId ->
                preloadCache().map { counterId }
            }
            .flowOn(dispatcherProvider.default())
    }

    override fun deletePlayerProfile(profileName: String): Flow<Boolean> {
        invalidateCache()
        return flow {
            database.templateDao().deletePlayerProfile(profileName)
            emit(true)
        }
            .flowOn(dispatcherProvider.default())
    }

    private suspend fun addCounterTemplateInternal(
        counterTemplate: CounterTemplateModel
    ): Int {
        return database.templateDao().insert(
            CounterTemplateEntity(counterTemplate)
        ).toInt()
    }

    private suspend fun addCounterTemplatesInternal(
        counterTemplates: List<CounterTemplateModel>
    ): List<Int> {
        return database.templateDao().insertCounters(
            counterTemplates.map {
                CounterTemplateEntity(it)
            }
        ).map { it.toInt() }
    }

    override fun preloadCache(): Flow<Boolean> {
        return getAllPlayerProfiles().zip(getAllCounters()) { _, _ ->
            true
        }
    }

    private fun invalidateCache() {
        cachedPlayerProfiles = null
        cachedCounterTemplates = null
    }
}

