package com.kenkeremath.mtgcounter.view.counter.edit

interface PlayerMenuListener {
    fun onEditCountersOpened(playerId: Int)
    fun onRearrangeCountersOpened(playerId: Int)
    fun onRollOpened(playerId: Int)
    fun onCloseSubMenu(playerId: Int)
    fun onCounterSelected(playerId: Int, templateId: Int)
    fun onCounterDeselected(playerId: Int, templateId: Int)
    fun onCounterRearranged(playerId: Int, templateId: Int, oldPosition: Int, newPosition: Int)
    fun onCancelCounterChanges(playerId: Int)
    fun onConfirmCounterChanges(playerId: Int)
}