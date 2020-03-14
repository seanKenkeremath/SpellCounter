package com.kenkeremath.mtgcounter.dagger

import com.kenkeremath.mtgcounter.SpellCounterApplication
import com.kenkeremath.mtgcounter.ui.dagger.MainActivityModule
import com.kenkeremath.mtgcounter.ui.setup.dagger.SetupFragmentModule

import javax.inject.Singleton

import dagger.Component
import dagger.android.AndroidInjectionModule

@Singleton
@Component(modules = [AndroidInjectionModule::class, ApplicationModule::class, MainActivityModule::class, SetupFragmentModule::class])
interface ApplicationComponent {
    fun inject(application: SpellCounterApplication)
}

