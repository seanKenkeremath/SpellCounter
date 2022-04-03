package com.kenkeremath.mtgcounter.ui.setup.theme

import android.content.Intent
import android.os.Bundle
import com.kenkeremath.mtgcounter.databinding.ActivityThemeBinding
import com.kenkeremath.mtgcounter.ui.BaseActivity
import com.kenkeremath.mtgcounter.ui.MainActivity

class ThemeActivity : BaseActivity() {

    private lateinit var binding: ActivityThemeBinding

    private var themeChanged: Boolean = false

    companion object {
        private const val STATE_THEME_CHANGED = "state_theme_changed"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThemeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val initialTheme = datastore.theme
        themeChanged = savedInstanceState?.getBoolean(STATE_THEME_CHANGED, false) ?: false

        binding.lightTheme.tag = SpellCounterTheme.LIGHT
        binding.darkTheme.tag = SpellCounterTheme.DARK

        val themeViews = listOf(
            binding.lightTheme,
            binding.darkTheme
        )

        for (themeView in themeViews) {
            val theme = themeView.tag
            if (theme is SpellCounterTheme) {
                val resolvedTheme = ScThemeUtils.resolveTheme(this, datastore.theme)
                themeView.isSelected = theme == resolvedTheme
                themeView.setOnClickListener {
                    if (theme != resolvedTheme) {
                        datastore.theme = theme
                        themeChanged = theme != initialTheme
                        recreate()
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_THEME_CHANGED, themeChanged)
    }

    override fun onBackPressed() {
        if (themeChanged) {
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            finish()
        } else {
            super.onBackPressed()
        }
    }
}