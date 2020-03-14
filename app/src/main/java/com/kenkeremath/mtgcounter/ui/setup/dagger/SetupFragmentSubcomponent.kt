package com.kenkeremath.mtgcounter.ui.setup.dagger

import com.kenkeremath.mtgcounter.ui.setup.SetupFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent()
interface SetupFragmentSubcomponent : AndroidInjector<SetupFragment> {
    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<SetupFragment>
}