package com.kenkeremath.mtgcounter.ui

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.kenkeremath.mtgcounter.legacy.MigrationHelper
import com.kenkeremath.mtgcounter.persistence.ProfileRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    @Inject
    lateinit var migrationHelper: MigrationHelper

    @Inject
    lateinit var profilesRepository: ProfileRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (migrationHelper.needsMigration) {
            lifecycleScope.launch {
                migrationHelper.performMigration()
                    .collect {
                        proceed()
                    }
            }
        } else {
            proceed()
        }
    }

    private fun proceed() {
        lifecycleScope.launch {
            profilesRepository.preloadCache()
                .onCompletion {
                    startActivity(Intent(this@SplashActivity, ComposeActivity::class.java))
                    finish()
                }
                .collect { }
        }
    }
}