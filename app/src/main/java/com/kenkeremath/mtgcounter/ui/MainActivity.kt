package com.kenkeremath.mtgcounter.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.ui.setup.SetupFragment
import dagger.android.AndroidInjection

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SetupFragment.newInstance())
                .commitNow()
        }
    }

}
