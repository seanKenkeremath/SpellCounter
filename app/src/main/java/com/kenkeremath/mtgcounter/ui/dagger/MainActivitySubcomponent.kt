package com.kenkeremath.mtgcounter.ui.dagger

import com.kenkeremath.mtgcounter.ui.MainActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent()
interface MainActivitySubcomponent : AndroidInjector<MainActivity> {
    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<MainActivity>
}