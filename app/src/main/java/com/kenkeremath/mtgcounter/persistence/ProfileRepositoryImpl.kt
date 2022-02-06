package com.kenkeremath.mtgcounter.persistence

import com.kenkeremath.mtgcounter.coroutines.DefaultDispatcherProvider
import com.kenkeremath.mtgcounter.coroutines.DispatcherProvider
import com.kenkeremath.mtgcounter.model.counter.CounterSymbol
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.model.player.PlayerTemplateModel
import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerCounterTemplateCrossRefEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerTemplateEntity
import kotlinx.coroutines.flow.*
import javax.inject.Inject


class ProfileRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val datastore: Datastore,
    private val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider()
) : ProfileRepository {

    private var cachedPlayerTemplates: List<PlayerTemplateModel>? = null
    private var cachedCounterTemplates: List<CounterTemplateModel>? = null

    override fun getAllPlayerTemplates(): Flow<List<PlayerTemplateModel>> {
        return cachedPlayerTemplates?.let {
            flowOf(it)
        } ?: flow {
            val entities = database.templateDao().getPlayerTemplates()
            val templates = entities.map {
                PlayerTemplateModel(it)
            }
            cachedPlayerTemplates = templates
            emit(templates)
        }.flowOn(dispatcherProvider.default())
    }

    override fun getPlayerTemplateByName(profileName: String): Flow<PlayerTemplateModel?> {
        return getAllPlayerTemplates().map { profiles ->
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
        return getPlayerTemplateByName(profileName).map {
            it?.counters ?: emptyList()
        }
    }

    override fun addPlayerTemplate(playerTemplate: PlayerTemplateModel): Flow<Boolean> {
        invalidateCache()
        return flow {
            addPlayerTemplateInternal(playerTemplate)
            emit(true)
        }
            .flatMapConcat { preloadCache() }
            .flowOn(dispatcherProvider.default())
    }

    private suspend fun addPlayerTemplateInternal(
        playerTemplate: PlayerTemplateModel
    ) {
        val playerEntity =
            PlayerTemplateEntity(
                name = playerTemplate.name,
                deletable = playerTemplate.deletable
            )
        database.templateDao().deletePlayerCounterCrossRefsForPlayerTemplate(playerTemplate.name)
        database.templateDao().replacePlayerTemplate(playerEntity)
        for (counterTemplate in playerTemplate.counters) {
            val counterId =
                addCounterTemplateInternal(counterTemplate)
            database.templateDao().insert(
                PlayerCounterTemplateCrossRefEntity(
                    playerTemplateId = playerEntity.name,
                    counterTemplateId = counterId
                )
            )
        }
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

    override fun addCounterTemplateToProfile(
        counterTemplate: CounterTemplateModel,
        profileName: String
    ): Flow<Int> {
        invalidateCache()
        return flow {
            val id = addCounterTemplateInternal(counterTemplate)
            database.templateDao().insert(
                PlayerCounterTemplateCrossRefEntity(
                    playerTemplateId = profileName,
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

    override fun deletePlayerTemplate(profileName: String): Flow<Boolean> {
        invalidateCache()
        return flow {
            database.templateDao().deletePlayerTemplate(profileName)
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

    override fun createStockTemplates(): Flow<Boolean> {
        val stockCounterTemplates = mutableListOf<CounterTemplateModel>()
        for (counterSymbol in CounterSymbol.values().filter { it.resId != null }) {
            stockCounterTemplates.add(
                CounterTemplateModel(
                    symbol = counterSymbol,
                    deletable = false
                )
            )
        }
        //This one is deletable
        stockCounterTemplates.add(CounterTemplateModel(name = "XP"))

        val stockPlayerProfile = PlayerTemplateModel(
            name = PlayerTemplateModel.NAME_DEFAULT,
            counters = stockCounterTemplates,
            deletable = false
        )
        return flow {
            addPlayerTemplateInternal(stockPlayerProfile)
            emit(true)
        }.flowOn(dispatcherProvider.default())
    }

    override fun preloadCache(): Flow<Boolean> {
        return getAllPlayerTemplates().zip(getAllCounters()) { _, _ ->
            true
        }
    }

    private fun invalidateCache() {
        cachedPlayerTemplates = null
        cachedCounterTemplates = null
    }
}

