package com.kenkeremath.mtgcounter.model.counter

import android.os.Parcelable
import com.kenkeremath.mtgcounter.util.CounterUtils
import kotlinx.parcelize.Parcelize

@Parcelize
data class CounterModel(
    var amount: Int = 0,
    val name: String? = null,
    val color: Int? = null,
    val uri: String? = null,
    val symbol: Int? = null,
    val templateId: Int = 0
) : Parcelable {

    constructor(template: CounterTemplateModel) : this(
        name = template.name,
        color = template.color,
        uri = template.uri,
        symbol = template.symbol.resId,
        templateId = template.id,
    )
}