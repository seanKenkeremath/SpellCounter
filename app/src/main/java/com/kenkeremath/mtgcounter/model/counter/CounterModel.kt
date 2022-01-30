package com.kenkeremath.mtgcounter.model.counter

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CounterModel(
    var amount: Int = 0,
    val template: CounterTemplateModel
) : Parcelable