package com.kenkeremath.mtgcounter.view.counter.edit

import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel

data class CounterSelectionUiModel(
    val template: CounterTemplateModel,
    val selected: Boolean = false,
)