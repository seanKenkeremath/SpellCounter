package com.kenkeremath.mtgcounter.ui.game.dagger

import com.kenkeremath.mtgcounter.ui.game.GameActivity
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [GameActivitySubcomponent::class])
abstract class GameActivityModule {
    @Binds
    @IntoMap
    @ClassKey(GameActivity::class)
    internal abstract fun bindGameAndroidInjectorFactory(factory: GameActivitySubcomponent.Factory): AndroidInjector.Factory<*>
}