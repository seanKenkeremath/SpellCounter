package com.kenkeremath.mtgcounter.model.player

import android.os.Parcelable
import androidx.annotation.ColorRes
import com.kenkeremath.mtgcounter.model.counter.CounterModel
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayerModel(
    val id: Int,
    val life: Int = 0,
    @ColorRes val colorResId: Int = 0,
    val lifeCounter: CounterTemplateModel?,
    val counters: List<CounterModel> = emptyList(),
) : Parcelable