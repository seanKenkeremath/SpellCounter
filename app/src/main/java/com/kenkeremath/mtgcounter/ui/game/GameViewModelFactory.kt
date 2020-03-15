package com.kenkeremath.mtgcounter.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kenkeremath.mtgcounter.persistence.GameRepository
import javax.inject.Inject

class GameViewModelFactory @Inject constructor(private val gameRepository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(gameRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}