package com.kenkeremath.mtgcounter.ui.settings.counters.edit

data class CreateCounterTypeSpinnerUiModel(
    val type: CreateCounterType,
    val label: String,
    val selected: Boolean,
) {
    override fun toString(): String {
        return label
    }
}