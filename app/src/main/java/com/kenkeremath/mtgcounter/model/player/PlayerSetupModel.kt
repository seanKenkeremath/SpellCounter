package com.kenkeremath.mtgcounter.model.player

import androidx.annotation.ColorRes

data class PlayerSetupModel (
    var template: PlayerTemplateModel? = null,
    @ColorRes var colorResId: Int? = null
)