package com.kenkeremath.mtgcounter.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.counter.CounterSymbol
import com.kenkeremath.mtgcounter.persistence.AppDatabase
import com.kenkeremath.mtgcounter.persistence.Datastore
import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity
import dagger.android.AndroidInjection
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashActivity: AppCompatActivity() {

    @Inject
    lateinit var datastore: Datastore

    @Inject
    lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        if (datastore.isFirstLaunch) {
            val stockColors = resources.getIntArray(R.array.counter_palette)
            val stockTemplates = mutableListOf<CounterTemplateEntity>()
            for (counterSymbol in CounterSymbol.values().filter { it.resId != null }) {
                stockTemplates.add(CounterTemplateEntity(symbolOrdinal = counterSymbol.ordinal))
            }
            for (stockColor in stockColors) {
                stockTemplates.add(CounterTemplateEntity(color = stockColor))
            }
            //TODO: remove
            stockTemplates.add(CounterTemplateEntity(name = "TST"))
            stockTemplates.add(CounterTemplateEntity(name = "TST2"))
            stockTemplates.add(CounterTemplateEntity(name = "TST3"))
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