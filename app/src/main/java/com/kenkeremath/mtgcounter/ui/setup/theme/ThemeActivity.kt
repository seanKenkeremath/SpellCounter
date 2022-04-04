package com.kenkeremath.mtgcounter.ui.setup.theme

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.kenkeremath.mtgcounter.R
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
        binding.moxEmeraldTheme.tag = SpellCounterTheme.MOX_EMERALD
        binding.lotusPetalTheme.tag = SpellCounterTheme.LOTUS_PETAL
        binding.aetherHubTheme.tag = SpellCounterTheme.AETHERHUB
        binding.pinkTheme.tag = SpellCounterTheme.PINK
        binding.karnTheme.tag = SpellCounterTheme.KARN

        val themeViews = listOf(
            binding.lightTheme,
            binding.darkTheme,
            binding.moxEmeraldTheme,
            binding.lotusPetalTheme,
            binding.aetherHubTheme,
            binding.pinkTheme,
            binding.karnTheme,
        )

        for (themeView in themeViews) {
            val theme = themeView.tag
            if (theme is SpellCounterTheme) {

                //Special case
                if (theme == SpellCounterTheme.AETHERHUB) {
                    val spannable = SpannableString(themeView.label)
                    val spanSection = "hub"
                    val startIndex = spannable.indexOf(spanSection)
                    if (startIndex != -1) {
                        spannable.setSpan(
                            BackgroundColorSpan(
                                ContextCompat.getColor(
                                    this,
                                    R.color.aether_hub_primary
                                )
                            ),
                            startIndex,
                            startIndex + spanSection.length,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                        spannable.setSpan(
                            ForegroundColorSpan(
                                ContextCompat.getColor(
                                    this,
                                    R.color.dark_mode_black
                                )
                            ),
                            startIndex,
                            startIndex + spanSection.length,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                        themeView.label = spannable
                    }
                }
                //End special case

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