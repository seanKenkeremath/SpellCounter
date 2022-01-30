package com.kenkeremath.mtgcounter.ui.settings.profiles.manage

import com.kenkeremath.mtgcounter.model.player.PlayerTemplateModel

data class ProfileUiModel(
    val name: String,
    val deletable: Boolean = true,
) {
    constructor(playerTemplateModel: PlayerTemplateModel): this(
        name = playerTemplateModel.name,
        deletable = playerTemplateModel.deletable,
    )
}