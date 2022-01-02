package com.kenkeremath.mtgcounter.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kenkeremath.mtgcounter.model.counter.CounterColor
import com.kenkeremath.mtgcounter.model.counter.CounterSymbol
import com.kenkeremath.mtgcounter.persistence.AppDatabase
import com.kenkeremath.mtgcounter.persistence.Datastore
import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity: AppCompatActivity() {

    @Inject
    lateinit var datastore: Datastore

    @Inject
    lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (datastore.isFirstLaunch) {
            val stockTemplates = mutableListOf<CounterTemplateEntity>()
            for (counterSymbol in CounterSymbol.values().filter { it.resId != null }) {
                stockTemplates.add(CounterTemplateEntity(symbolId = counterSymbol.symbolId))
            }
            stockTemplates.add(CounterTemplateEntity(name = "XP"))
            stockTemplates.add(CounterTemplateEntity(name = "CMD"))
            for (stockColor in CounterColor.allColors()) {
                stockTemplates.add(CounterTemplateEntity(colorId = stockColor.colorId))
            }
            lifecycleScope.launch {
                database.templateDao().insertCounters(
                    stockTemplates
                )
                datastore.setFirstLaunchComplete()
                proceed()
            }
        } else {
            proceed()
        }
    }

    private fun proceed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}