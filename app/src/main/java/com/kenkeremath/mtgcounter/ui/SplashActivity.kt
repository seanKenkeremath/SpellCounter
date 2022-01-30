package com.kenkeremath.mtgcounter.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kenkeremath.mtgcounter.persistence.Datastore
import com.kenkeremath.mtgcounter.persistence.ProfileRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var datastore: Datastore

    @Inject
    lateinit var profilesRepository: ProfileRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (datastore.isFirstLaunch) {
            lifecycleScope.launch {
                profilesRepository.createStockTemplates()
                    .catch {
                        //TODO: error handling?
                        finish()
                    }
                    .collect {
                        datastore.setFirstLaunchComplete()
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
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
                .collect { }
        }
    }
}