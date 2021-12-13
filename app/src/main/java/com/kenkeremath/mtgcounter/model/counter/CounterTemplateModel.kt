package com.kenkeremath.mtgcounter.model.counter

import android.graphics.Color
import androidx.annotation.ColorInt
import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity

data class CounterTemplateModel(
    val id: Int = 0,
    var name: String? = null,
    @ColorInt var color: Int? = null,
    var symbol: CounterSymbol = CounterSymbol.NONE,
    var uri: String? = null,
    var deletable: Boolean = false,
) {
    constructor(entity: CounterTemplateEntity) : this(
        id = entity.id,
        name = entity.name,
        color = entity.color,
        symbol = CounterSymbol.values().find { it.symbolId == entity.symbolId } ?: CounterSymbol.NONE,
        uri = entity.uri,
        deletable = entity.deletable
    )

    override fun toString(): String {
        return "CounterTemplateModel(id=$id, name=$name, color=$color, symbol=${symbol.name}, uri=$uri)"
    }
}