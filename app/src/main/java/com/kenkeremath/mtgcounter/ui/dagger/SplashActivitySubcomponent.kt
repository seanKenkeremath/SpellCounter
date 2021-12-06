package com.kenkeremath.mtgcounter.ui.dagger

import com.kenkeremath.mtgcounter.ui.SplashActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent()
interface SplashActivitySubcomponent : AndroidInjector<SplashActivity> {
    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<SplashActivity>
}