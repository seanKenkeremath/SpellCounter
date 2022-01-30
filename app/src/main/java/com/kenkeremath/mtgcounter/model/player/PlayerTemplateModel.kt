package com.kenkeremath.mtgcounter.model.player

import android.os.Parcelable
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.persistence.entities.PlayerTemplateWithCountersEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayerTemplateModel(
    val name: String = "",
    val counters: List<CounterTemplateModel> = emptyList(),
    val deletable: Boolean = true,
) : Parcelable, Comparable<PlayerTemplateModel> {
    companion object {
        const val NAME_DEFAULT = "Default"
    }

    constructor(entity: PlayerTemplateWithCountersEntity) : this(
        entity.template.name,
        entity.counters.map { CounterTemplateModel(it) },
        entity.template.deletable,
    )

    override fun toString(): String {
        return "PlayerTemplateModel(name='$name', counters=$counters)"
    }

    override fun compareTo(other: PlayerTemplateModel): Int {
        return if (name == NAME_DEFAULT) {
            -1
        } else if (other.name == NAME_DEFAULT) {
            1
        } else {
            name.compareTo(other.name)
        }
    }
}