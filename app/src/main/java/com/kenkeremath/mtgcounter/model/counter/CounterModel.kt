package com.kenkeremath.mtgcounter.model.counter

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class CounterModel(
    var amount: Int = 0,
    val template: CounterTemplateModel
) : Parcelable