package com.kenkeremath.mtgcounter.ui.game

interface OnPlayerClickedListener {
    //TODO: more callbacks
    fun onLifeIncremented(playerIndex: Int, amount: Int)
}