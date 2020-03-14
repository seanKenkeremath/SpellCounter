package com.kenkeremath.mtgcounter

import android.app.Application
import com.kenkeremath.mtgcounter.dagger.ApplicationModule
import com.kenkeremath.mtgcounter.dagger.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject



class SpellCounterApplication : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

    override fun onCreate() {
        super.onCreate()
        DaggerApplicationComponent.builder().applicationModule(ApplicationModule(this)).build().inject(this)
    }
}