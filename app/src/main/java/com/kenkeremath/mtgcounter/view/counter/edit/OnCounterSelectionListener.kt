package com.kenkeremath.mtgcounter.view.counter.edit

interface OnCounterSelectionListener {
    fun onCounterSelected(playerId: Int, templateId: Int)
    fun onCounterDeselected(playerId: Int, templateId: Int)
    fun onCancelCounterChanges(playerId: Int)
    fun onConfirmCounterChanges(playerId: Int)
}