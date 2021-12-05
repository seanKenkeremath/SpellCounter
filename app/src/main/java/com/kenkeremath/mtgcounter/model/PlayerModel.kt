package com.kenkeremath.mtgcounter.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayerModel(
    val id: Int,
    val life: Int = 0,
    val color: Int = 0,
    val counters: List<CounterModel> = emptyList(),
    val templateId: Int? = null
): Parcelable