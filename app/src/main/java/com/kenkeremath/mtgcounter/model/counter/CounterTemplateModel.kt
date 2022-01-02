package com.kenkeremath.mtgcounter.model.counter

import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity

data class CounterTemplateModel(
    val id: Int = 0,
    var name: String? = null,
    var color: CounterColor = CounterColor.NONE,
    var symbol: CounterSymbol = CounterSymbol.NONE,
    var uri: String? = null,
    var deletable: Boolean = false,
) {
    constructor(entity: CounterTemplateEntity) : this(
        id = entity.id,
        name = entity.name,
        color = CounterColor.values().find { it.colorId == entity.colorId } ?: CounterColor.NONE,
        symbol = CounterSymbol.values().find { it.symbolId == entity.symbolId } ?: CounterSymbol.NONE,
        uri = entity.uri,
        deletable = entity.deletable
    )

    override fun toString(): String {
        return "CounterTemplateModel(id=$id, name=$name, color=$color, symbol=${symbol.name}, uri=$uri)"
    }
}