package com.kenkeremath.mtgcounter

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SpellCounterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}