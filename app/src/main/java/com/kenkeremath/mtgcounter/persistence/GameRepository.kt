package com.kenkeremath.mtgcounter.persistence

import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.model.template.CounterTemplateModel
import com.kenkeremath.mtgcounter.model.template.PlayerTemplateModel
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun getAllPlayerTemplates(): Flow<List<PlayerTemplateModel>>
    fun getAllCounters(): Flow<List<CounterTemplateModel>>
    fun addPlayerTemplate(playerTemplate: PlayerTemplateModel): Flow<Boolean>
    fun addCounterTemplate(counterTemplate: CounterTemplateModel): Flow<Boolean>
    fun createNewCounterTemplateId() : Int
    var startingLife : Int
    var numberOfPlayers: Int
    var keepScreenOn : Boolean
    var hideNavigation : Boolean
    var tabletopType: TabletopType
}