package com.kenkeremath.mtgcounter.model.player

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayerSetupModel (
    val id: Int = 0,
    val profile: PlayerProfileModel? = null,
    val color: PlayerColor = PlayerColor.NONE
) : Parcelable