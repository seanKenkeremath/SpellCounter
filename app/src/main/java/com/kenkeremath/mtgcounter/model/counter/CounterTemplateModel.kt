package com.kenkeremath.mtgcounter.model.counter

import android.os.Parcelable
import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class CounterTemplateModel(
    val id: Int = ID_NOT_SET,
    val name: String? = null,
    val color: CounterColor = CounterColor.NONE,
    val symbol: CounterSymbol = CounterSymbol.NONE,
    val uri: String? = null,
    val deletable: Boolean = true,
): Parcelable {

    companion object {
        //Room treats 0 as not set for auto-generating primary keys
        const val ID_NOT_SET = 0
    }
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