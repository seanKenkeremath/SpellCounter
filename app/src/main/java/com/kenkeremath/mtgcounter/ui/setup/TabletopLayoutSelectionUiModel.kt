package com.kenkeremath.mtgcounter.ui.setup

import com.kenkeremath.mtgcounter.model.TabletopType

data class TabletopLayoutSelectionUiModel(
    val tabletopType: TabletopType,
    val selected: Boolean,
)