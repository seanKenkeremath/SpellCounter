package com.kenkeremath.mtgcounter.ui.dagger

import com.kenkeremath.mtgcounter.ui.SplashActivity
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap


@Module (subcomponents = [SplashActivitySubcomponent::class])
abstract class SplashActivityModule {
    @Binds
    @IntoMap
    @ClassKey(SplashActivity::class)
    internal abstract fun bindMainAndroidInjectorFactory(factory: SplashActivitySubcomponent.Factory): AndroidInjector.Factory<*>
}