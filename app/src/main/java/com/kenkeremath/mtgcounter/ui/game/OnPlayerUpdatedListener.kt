package com.kenkeremath.mtgcounter.ui.game

import com.kenkeremath.mtgcounter.model.CounterModel

interface OnPlayerUpdatedListener {
    fun onLifeIncremented(playerId: Int, amountDifference: Int)
    fun onLifeAmountSet(playerId: Int, amount: Int)
    fun onCounterIncremented(playerId: Int, counterId: Int, amountDifference: Int)
    fun onCounterAmountSet(playerId: Int, counterId: Int, amount: Int)

    fun onCounterAdded(playerId: Int, counterModel: CounterModel)
    fun onCounterEdited(playerId: Int, counterPosition: Int, counterModel: CounterModel)
}