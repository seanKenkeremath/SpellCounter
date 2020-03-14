package com.kenkeremath.mtgcounter.ui.setup.dagger

import com.kenkeremath.mtgcounter.ui.setup.SetupFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [SetupFragmentSubcomponent::class])
abstract class SetupFragmentModule {
    @Binds
    @IntoMap
    @ClassKey(SetupFragment::class)
    internal abstract fun bindSetupAndroidInjectorFactory(factory: SetupFragmentSubcomponent.Factory): AndroidInjector.Factory<*>
}