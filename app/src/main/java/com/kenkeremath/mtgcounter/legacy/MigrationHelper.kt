package com.kenkeremath.mtgcounter.legacy

import com.kenkeremath.mtgcounter.coroutines.DefaultDispatcherProvider
import com.kenkeremath.mtgcounter.coroutines.DispatcherProvider
import com.kenkeremath.mtgcounter.model.counter.CounterSymbol
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.model.player.PlayerTemplateModel
import com.kenkeremath.mtgcounter.persistence.AppDatabase
import com.kenkeremath.mtgcounter.persistence.Datastore
import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerCounterTemplateCrossRefEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerTemplateEntity
import com.kenkeremath.mtgcounter.util.LogUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.lang.Exception

class MigrationHelper(
    private val datastore: Datastore,
    private val database: AppDatabase,
    private val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider()
) {

    private val legacyStockPlayerTemplateNames: Set<String> = setOf(
        "Standard",
        "Modern",
        "EDH",
        PlayerTemplateModel.NAME_DEFAULT,
    )

    private val legacyStockCounterTemplateNames: Set<String> = setOf(
        "NRG",
        "PSN",
        "STRM",
        "MANA",
        "CMD",
        "XP",
    )

    val needsMigration: Boolean
        get() = datastore.version < Datastore.CURRENT_VERSION

    //Includes setup for first launch (no existing install)
    fun performMigration(): Flow<Boolean> {
        return flow {
            val oldVersion = datastore.version

            if (oldVersion < Datastore.VERSION_3_0) {
                //Stock template creation is mandatory, migration from older version can fail silently
                createStockTemplates()
                datastore.updateVersion()

                try {
                    migrateTo3_0_0()
                    datastore.updateVersion()
                } catch (e: Exception) {
                    LogUtils.d(tag = LogUtils.TAG_MIGRATION, message = "Migration failed: $e")
                }
            }
            emit(true)
        }.flowOn(dispatcherProvider.io())
    }

    private suspend fun createStockTemplates() {
        LogUtils.d(tag = LogUtils.TAG_MIGRATION, message = "Creating Stock Templates")

        //Create stock counters (symbols)
        val stockCounterTemplates = mutableListOf<CounterTemplateModel>()
        for (counterSymbol in CounterSymbol.values().filter { it.resId != null }) {
            stockCounterTemplates.add(
                CounterTemplateModel(
                    symbol = counterSymbol,
                    deletable = false
                )
            )
        }
        //This one is deletable
        stockCounterTemplates.add(CounterTemplateModel(name = "XP"))


        //insert player
        val playerEntity =
            PlayerTemplateEntity(
                name = PlayerTemplateModel.NAME_DEFAULT,
                deletable = false
            )
        LogUtils.d(tag = LogUtils.TAG_MIGRATION, message = "Stock Profile to create: $playerEntity")
        database.templateDao().insert(playerEntity)


        //insert stock counters
        LogUtils.d(
            tag = LogUtils.TAG_MIGRATION,
            message = "Stock Counters to create: $stockCounterTemplates"
        )
        val counterIds = database.templateDao().insertCounters(
            stockCounterTemplates.map {
                CounterTemplateEntity(it)
            }
        )

        //create links between default player and counters
        val links = counterIds.map {
            PlayerCounterTemplateCrossRefEntity(
                playerEntity.name,
                it.toInt(),
            )
        }
        LogUtils.d(tag = LogUtils.TAG_MIGRATION, message = "Links to create: $links")
        database.templateDao().insertPlayerCounterPairings(links)
        LogUtils.d(tag = LogUtils.TAG_MIGRATION, message = "Stock templates created")
    }

    private suspend fun migrateTo3_0_0() {
        LogUtils.d(tag = LogUtils.TAG_MIGRATION, message = "Migrating to 3.0.0")
        val allLegacyPlayerTemplates = datastore.legacyPlayerTemplates

        // Do not migrate stock templates. Only user created ones
        val legacyPlayerTemplatesToImport = allLegacyPlayerTemplates.filter {
            !it.templateName.isNullOrBlank() && !legacyStockPlayerTemplateNames.contains(it.templateName)
        }

        //Only migrate counters with text labels (including from stock player templates. Ignore stock counter templates)
        //Remove duplicates
        val legacyCountersToImport = allLegacyPlayerTemplates.flatMap { it.counters ?: emptyList() }
            .filter { !it.name.isNullOrBlank() && !legacyStockCounterTemplateNames.contains(it.name) }
            .toMutableSet()

        //Add all counters from user created player templates (including counters with stock names)
        //Remove duplicates
        legacyCountersToImport.addAll(legacyPlayerTemplatesToImport.flatMap {
            it.counters ?: emptyList()
        }.toSet())

        val importedCounters = legacyCountersToImport.map {
            CounterTemplateEntity(
                name = it.name?.take(CounterTemplateModel.MAX_LABEL_SIZE),
                startingValue = it.startingValue,
                deletable = true,
            )
        }

        val importedPlayers = legacyPlayerTemplatesToImport
            .map {
                PlayerTemplateEntity(
                    name = it.templateName!!,
                    deletable = true
                )
            }

        LogUtils.d(tag = LogUtils.TAG_MIGRATION, message = "Profiles to import: $importedPlayers")
        LogUtils.d(tag = LogUtils.TAG_MIGRATION, message = "Counters to import: $importedCounters")

        //Insert templates themselves without links
        var counterIds = emptyList<Long>()
        if (importedCounters.isNotEmpty()) {
            LogUtils.d(tag = LogUtils.TAG_MIGRATION, message = "Importing Counters")
            counterIds = database.templateDao().insertCounters(importedCounters).toList()
        }

        if (importedPlayers.isNotEmpty()) {
            LogUtils.d(tag = LogUtils.TAG_MIGRATION, message = "Importing Profiles")
            database.templateDao().insertPlayers(importedPlayers)
        }

        //Create counter links to profiles
        val links: MutableList<PlayerCounterTemplateCrossRefEntity> = mutableListOf()
        for (legacyPlayerTemplateModel in legacyPlayerTemplatesToImport) {
            legacyPlayerTemplateModel.counters?.let { legacyCounters ->
                for (legacyCounter in legacyCounters) {
                    val counterIndex = legacyCountersToImport.indexOf(legacyCounter)
                    if (counterIndex != -1) {
                        val counterId = counterIds[counterIndex]
                        links.add(
                            PlayerCounterTemplateCrossRefEntity(
                                legacyPlayerTemplateModel.templateName!!,
                                counterId.toInt()
                            )
                        )
                    }
                }
            }
        }
        LogUtils.d(tag = LogUtils.TAG_MIGRATION, message = "Cross Links to Create: $links")
        if (links.isNotEmpty()) {
            database.templateDao().insertPlayerCounterPairings(links)
        }
    }
}