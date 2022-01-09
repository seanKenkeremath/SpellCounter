package com.kenkeremath.mtgcounter.model.player

import android.os.Parcelable
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.persistence.entities.PlayerTemplateWithCountersEntity
import kotlinx.parcelize.Parcelize

@Parcelize
class PlayerTemplateModel(
    val name: String = "",
    val counters: List<CounterTemplateModel> = emptyList()
) : Parcelable {
    constructor(entity: PlayerTemplateWithCountersEntity) : this(
        entity.template.name,
        entity.counters.map { CounterTemplateModel(it) })

    override fun toString(): String {
        return "PlayerTemplateModel(name='$name', counters=$counters)"
    }
}