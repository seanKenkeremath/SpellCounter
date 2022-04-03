package com.kenkeremath.mtgcounter.ui

import android.os.Bundle
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.ui.setup.SetupFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SetupFragment.newInstance())
                .commit()
        }
    }

}
