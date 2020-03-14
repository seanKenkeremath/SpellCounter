package com.kenkeremath.mtgcounter.persistence

import androidx.lifecycle.LiveData
import com.kenkeremath.mtgcounter.model.template.CounterTemplateModel
import com.kenkeremath.mtgcounter.model.template.PlayerTemplateModel
import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerTemplateWithCountersEntity

interface GameRepository {
    val allPlayerTemplatesEntity: LiveData<List<PlayerTemplateModel>>
    val allCountersEntity: LiveData<List<CounterTemplateModel>>
    suspend fun insert(playerTemplate: PlayerTemplateModel)
    suspend fun insert(counterTemplate: CounterTemplateModel)
    fun getNewCounterTemplateId() : Int
}