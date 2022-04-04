package com.kenkeremath.mtgcounter.legacy

import com.kenkeremath.mtgcounter.coroutines.DefaultDispatcherProvider
import com.kenkeremath.mtgcounter.coroutines.DispatcherProvider
import com.kenkeremath.mtgcounter.model.counter.CounterSymbol
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.model.player.PlayerProfileModel
import com.kenkeremath.mtgcounter.persistence.AppDatabase
import com.kenkeremath.mtgcounter.persistence.Datastore
import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerProfileCounterTemplateCrossRefEntity
import com.kenkeremath.mtgcounter.persistence.entities.PlayerProfileEntity
import com.kenkeremath.mtgcounter.ui.setup.theme.SpellCounterTheme
import com.kenkeremath.mtgcounter.util.LogUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MigrationHelper(
    private val datastore: Datastore,
    private val legacyDatastore: LegacyDatastore,
    private val database: AppDatabase,
    private val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider()
) {

    private val legacyStockPlayerTemplateNames: Set<String> = setOf(
        "Standard",
        "Modern",
        "EDH",
        //Treat this as a legacy default so the new "Default" is not overwritten
        PlayerProfileModel.NAME_DEFAULT,
    )

    private val legacyStockCounterTemplateNames: Set<String> = setOf(
        "NRG",
        "PSN",
        "STRM",
        "MANA",
        "CMD",
        "XP",
    )

    //For converting old themes prior to v3.0 (saved as Strings) to v3 themes
    private val legacyThemeMapping: Map<String, SpellCounterTheme> = mapOf(
        "GREY" to SpellCounterTheme.DARK,
        "PURPLE" to SpellCounterTheme.LOTUS_PETAL,
        "DARK_BLUE" to SpellCounterTheme.DARK,
        "DARK_GREEN" to SpellCounterTheme.MOX_EMERALD
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
                try {
                    migrateTo3_0_0()
                } catch (e: Exception) {
                    LogUtils.d(tag = LogUtils.TAG_MIGRATION, message = "Migration failed: $e")
                }
            }

            if (oldVersion < Datastore.VERSION_3_1) {
                /**
                 * Try to parse old theme string. If there is an existing, known entry we
                 * try to map to one of our current themes. otherwise, do nothing
                 */
                val oldTheme = legacyDatastore.getTheme()
                legacyDatastore.clearTheme()
                oldTheme?.let {
                    val newThemeMatch = legacyThemeMapping[oldTheme]
                    if (newThemeMatch != null) {
                        datastore.theme = newThemeMatch
                    }
                }
            }
            datastore.updateVersion()
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
            PlayerProfileEntity(
                name = PlayerProfileModel.NAME_DEFAULT,
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
            PlayerProfileCounterTemplateCrossRefEntity(
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
        val allLegacyPlayerTemplates = legacyDatastore.legacyPlayerTemplates

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
                PlayerProfileEntity(
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
        val links: MutableList<PlayerProfileCounterTemplateCrossRefEntity> = mutableListOf()
        for (legacyPlayerTemplateModel in legacyPlayerTemplatesToImport) {
            legacyPlayerTemplateModel.counters?.let { legacyCounters ->
                for (legacyCounter in legacyCounters) {
                    val counterIndex = legacyCountersToImport.indexOf(legacyCounter)
                    if (counterIndex != -1) {
                        val counterId = counterIds[counterIndex]
                        links.add(
                            PlayerProfileCounterTemplateCrossRefEntity(
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