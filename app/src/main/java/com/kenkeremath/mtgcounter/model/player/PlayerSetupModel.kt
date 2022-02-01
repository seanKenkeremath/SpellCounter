package com.kenkeremath.mtgcounter.model.player

import android.os.Parcelable
import com.kenkeremath.mtgcounter.model.counter.CounterColor
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayerSetupModel (
    val id: Int = 0,
    val template: PlayerTemplateModel? = null,
    val color: CounterColor = CounterColor.NONE
) : Parcelable