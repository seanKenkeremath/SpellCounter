package com.kenkeremath.mtgcounter.model

import com.kenkeremath.mtgcounter.model.template.PlayerTemplateModel

class PlayerSetupModel (
    var template: PlayerTemplateModel? = null,
    var color: Int = 0
)