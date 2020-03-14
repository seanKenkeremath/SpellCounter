package com.kenkeremath.mtgcounter.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kenkeremath.mtgcounter.persistence.GameRepository
import javax.inject.Inject

class SetupViewModelFactory @Inject constructor(val gameRepository: GameRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SetupViewModel::class.java)) {
            return SetupViewModel(gameRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}