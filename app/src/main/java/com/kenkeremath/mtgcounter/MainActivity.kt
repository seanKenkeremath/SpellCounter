package com.kenkeremath.mtgcounter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kenkeremath.mtgcounter.ui.setup.SetupFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SetupFragment.newInstance())
                .commitNow()
        }
    }

}
