package com.kenkeremath.mtgcounter.ui.setup.theme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kenkeremath.mtgcounter.databinding.ActivityThemeBinding

class ThemeActivity: AppCompatActivity() {

    private lateinit var binding: ActivityThemeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThemeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}