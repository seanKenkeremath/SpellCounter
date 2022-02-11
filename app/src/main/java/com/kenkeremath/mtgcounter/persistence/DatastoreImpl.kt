package com.kenkeremath.mtgcounter.persistence

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import com.kenkeremath.mtgcounter.legacy.model.LegacyPlayerTemplateModel
import com.kenkeremath.mtgcounter.model.TabletopType
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.IOException

class DatastoreImpl(context: Context, private val moshi: Moshi) : Datastore {

    companion object {

        private const val PREFERENCES_NAME = "com.kenkeremath.sean"

        //Legacy Keys
        private const val KEY_LEGACY_GAME_STATE = "key_game_state"

        // Not necessary anymore, can just use DS Version
        private const val KEY_LEGACY_FIRST_LAUNCH = "key_first_launch"

        //There was a time before stock game templates existed
        private const val KEY_LEGACY_STOCK_GAME_TEMPLATES_SET = "key_stock_game_templates"
        private const val KEY_LEGACY_PLAYER_TEMPLATES = "key_templates"
        private const val KEY_LEGACY_GAME_TEMPLATES = "key_game_templates"
        private const val KEY_LEGACY_THEME = "key_theme"
        //End Legacy Keys

        private const val KEY_STARTING_LIFE = "key_starting_life"
        private const val KEY_NUMBER_PLAYERS = "key_number_Players"
        private const val KEY_KEEP_SCREEN_ON = "key_keep_screen_on"
        private const val KEY_HIDE_NAVIGATION = "key_hide_navigation"
        private const val KEY_TABLETOP_TYPE = "key_tabletop_type"


        private const val KEY_DATASTORE_VERSION = "key_datastore_version"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Activity.MODE_PRIVATE
    )

    @VisibleForTesting
    fun setLegacyTemplates(templates: List<LegacyPlayerTemplateModel>) {
        val type = Types.newParameterizedType(
            List::class.java,
            LegacyPlayerTemplateModel::class.java
        )
        val adapter = moshi.adapter<List<LegacyPlayerTemplateModel>>(type)
        val json = adapter.toJson(templates)
        prefs.edit().putString(KEY_LEGACY_PLAYER_TEMPLATES, json).apply()
    }

    //Legacy
    override val legacyPlayerTemplates: List<LegacyPlayerTemplateModel>
        get() {
            val returnList = mutableListOf<LegacyPlayerTemplateModel>()
            val storedJson = prefs.getString(KEY_LEGACY_PLAYER_TEMPLATES, null)
            if (storedJson != null) {
                try {
                    val type = Types.newParameterizedType(
                        List::class.java,
                        LegacyPlayerTemplateModel::class.java
                    )
                    val adapter = moshi.adapter<List<LegacyPlayerTemplateModel>>(type)
                    val storedList = adapter.fromJson(storedJson)
                    if (storedList != null) {
                        returnList.addAll(storedList)
                    }
                } catch (ignored: IOException) {
                }
            }
            return returnList
        }

    private fun getEditor(): SharedPreferences.Editor {
        return prefs.edit()
    }

    override var startingLife: Int
        get() = prefs.getInt(KEY_STARTING_LIFE, 20)
        set(value) = getEditor().putInt(KEY_STARTING_LIFE, value).apply()
    override var numberOfPlayers: Int
        get() = prefs.getInt(KEY_NUMBER_PLAYERS, 1)
        set(value) = getEditor().putInt(KEY_NUMBER_PLAYERS, value).apply()
    override var keepScreenOn: Boolean
        get() = prefs.getBoolean(KEY_KEEP_SCREEN_ON, true)
        set(value) = getEditor().putBoolean(KEY_KEEP_SCREEN_ON, value).apply()
    override var hideNavigation: Boolean
        get() = prefs.getBoolean(KEY_HIDE_NAVIGATION, false)
        set(value) = getEditor().putBoolean(KEY_HIDE_NAVIGATION, value).apply()
    override var tabletopType: TabletopType
        get() = TabletopType.values()[prefs.getInt(KEY_TABLETOP_TYPE, TabletopType.LIST.ordinal)]
        set(value) = getEditor().putInt(KEY_TABLETOP_TYPE, value.ordinal).apply()

    override val version: Int
        get() = prefs.getInt(KEY_DATASTORE_VERSION, 0)

    override fun updateVersion() {
        getEditor().putInt(KEY_DATASTORE_VERSION, Datastore.CURRENT_VERSION).apply()
    }
}