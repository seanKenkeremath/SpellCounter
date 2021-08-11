package com.kenkeremath.mtgcounter.ui.game

interface OnPlayerUpdatedListener {
    fun onLifeIncremented(playerId: Int, amountDifference: Int)
    fun onLifeAmountSet(playerId: Int, amount: Int)
    fun onCounterIncremented(playerId: Int, counterId: Int, amountDifference: Int)
    fun onCounterAmountSet(playerId: Int, counterId: Int, amount: Int)

    //TODO
    fun onCounterAdded(playerId: Int)
}