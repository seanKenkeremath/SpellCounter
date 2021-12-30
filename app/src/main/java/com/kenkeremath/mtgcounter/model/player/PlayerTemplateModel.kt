package com.kenkeremath.mtgcounter.model.player

import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.persistence.entities.PlayerTemplateWithCountersEntity

class PlayerTemplateModel(
    val name: String = "",
    val counters: List<CounterTemplateModel> = emptyList()
) {
    constructor(entity: PlayerTemplateWithCountersEntity) : this(
        entity.template.name,
        entity.counters.map { CounterTemplateModel(it) })

    override fun toString(): String {
        return "PlayerTemplateModel(name='$name', counters=$counters)"
    }
}