package com.kenkeremath.mtgcounter.persistence

import com.kenkeremath.mtgcounter.coroutines.DefaultDispatcherProvider
import com.kenkeremath.mtgcounter.coroutines.DispatcherProvider
import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.model.template.CounterTemplateModel
import com.kenkeremath.mtgcounter.model.template.PlayerTemplateModel
import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerCounterTemplateCrossRefEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerTemplateEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class GameRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val datastore: Datastore,
    private val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider()
) : GameRepository {
    override var startingLife: Int
        get() = datastore.startingLife
        set(value) {
            datastore.startingLife = value
        }
    override var numberOfPlayers: Int
        get() = datastore.numberOfPlayers
        set(value) {
            datastore.numberOfPlayers = value
        }
    override var keepScreenOn: Boolean
        get() = datastore.keepScreenOn
        set(value) {
            datastore.keepScreenOn = value
        }
    override var tabletopType: TabletopType
        get() = datastore.tabletopType
        set(value) {
            datastore.tabletopType = value
        }
    override var hideNavigation: Boolean
        get() = datastore.keepScreenOn
        set(value) {
            datastore.keepScreenOn = value
        }

    override fun getAllPlayerTemplates(): Flow<List<PlayerTemplateModel>> {
        return flow {
            val entities = database.templateDao().getPlayerTemplates()
            emit(entities.map {
                PlayerTemplateModel(it)
            })
        }.flowOn(dispatcherProvider.default())
    }

    override fun getAllCounters(): Flow<List<CounterTemplateModel>> {
        return flow {
            val entities = database.templateDao().getCounterTemplates()
            emit(entities.map {
                CounterTemplateModel(it)
            })
        }.flowOn(dispatcherProvider.default())
    }

    override fun addPlayerTemplate(playerTemplate: PlayerTemplateModel): Flow<Boolean> {
        return flow {
            val playerEntity = PlayerTemplateEntity(name = playerTemplate.name)
            database.templateDao().replacePlayerTemplate(playerEntity)
            for (counterTemplate in playerTemplate.counters) {
                addCounterTemplateInternal(counterTemplate)
                database.templateDao().insert(
                    PlayerCounterTemplateCrossRefEntity(
                        playerTemplateId = playerEntity.name,
                        counterTemplateId = counterTemplate.id
                    )
                )
            }
            emit(true)
        }
            .flowOn(dispatcherProvider.default())
    }

    private suspend fun addCounterTemplateInternal(counterTemplate: CounterTemplateModel) {
        database.templateDao().insert(
            CounterTemplateEntity(
                id = counterTemplate.id,
                startingAmount = counterTemplate.startingAmount,
                name = counterTemplate.name,
                color = counterTemplate.color,
                linkToPlayer = false
            )
        )
    }

    override fun addCounterTemplate(counterTemplate: CounterTemplateModel): Flow<Boolean> {
        return flow {
            addCounterTemplateInternal(counterTemplate)
            emit(true)
        }.flowOn(dispatcherProvider.default())
    }

    override fun createNewCounterTemplateId(): Int {
        return datastore.getNewCounterTemplateId()
    }
}

