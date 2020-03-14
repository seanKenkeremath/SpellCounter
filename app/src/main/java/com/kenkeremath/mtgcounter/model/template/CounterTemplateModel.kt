package com.kenkeremath.mtgcounter.model.template

import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity

data class CounterTemplateModel(
    val id: Int = 0,
    var startingValue: Int = 0,
    var name: String? = null,
    var color: Int = 0
) {
    constructor(entity: CounterTemplateEntity) : this(entity.id, entity.startingValue, entity.name, entity.color)

    override fun toString(): String {
        return "CounterTemplateModel(id=$id, startingValue=$startingValue, name=$name, color=$color)"
    }
}