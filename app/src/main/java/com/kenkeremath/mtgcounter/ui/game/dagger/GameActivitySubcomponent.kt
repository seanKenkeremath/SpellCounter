package com.kenkeremath.mtgcounter.ui.game.dagger

import com.kenkeremath.mtgcounter.ui.game.GameActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface GameActivitySubcomponent : AndroidInjector<GameActivity> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<GameActivity>

}