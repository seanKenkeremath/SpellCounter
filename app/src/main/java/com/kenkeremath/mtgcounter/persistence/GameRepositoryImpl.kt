package com.kenkeremath.mtgcounter.persistence

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.model.template.CounterTemplateModel
import com.kenkeremath.mtgcounter.model.template.PlayerTemplateModel
import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerCounterTemplateCrossRefEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerTemplateEntity
import javax.inject.Inject


class GameRepositoryImpl @Inject constructor(private val database: AppDatabase, private val datastore: Datastore) : GameRepository {
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

    override fun createNewCounterTemplateId(): Int {
        return datastore.getNewCounterTemplateId()
    }

    override val allCountersEntity: LiveData<List<CounterTemplateModel>> =
        Transformations.map(database.templateDao().getCounterTemplates()) {
            it.map(::CounterTemplateModel)
        }

    override val allPlayerTemplatesEntity: LiveData<List<PlayerTemplateModel>> =
        Transformations.map(
            database.templateDao().getPlayerTemplates()) {
            it.map(::PlayerTemplateModel)
        }

    override suspend fun insert(counterTemplate: CounterTemplateModel) {
        database.templateDao().insert(
            CounterTemplateEntity(
                id = counterTemplate.id,
                startingValue = counterTemplate.startingValue,
                name = counterTemplate.name,
                color = counterTemplate.color,
                linkToPlayer = false
            )
        )
    }

    override suspend fun insert(playerTemplate: PlayerTemplateModel) {
        val playerEntity = PlayerTemplateEntity(name = playerTemplate.name)
        database.templateDao().replacePlayerTemplate(playerEntity)
        for (counterTemplate in playerTemplate.counters) {
            insert(counterTemplate)
            database.templateDao().insert(
                PlayerCounterTemplateCrossRefEntity(
                    playerTemplateId = playerEntity.name,
                    counterTemplateId = counterTemplate.id
                )
            )
        }
    }
}

