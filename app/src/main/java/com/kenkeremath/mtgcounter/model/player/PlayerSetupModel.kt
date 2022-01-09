package com.kenkeremath.mtgcounter.model.player

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayerSetupModel (
    var template: PlayerTemplateModel? = null,
    @ColorRes var colorResId: Int? = null
) : Parcelable