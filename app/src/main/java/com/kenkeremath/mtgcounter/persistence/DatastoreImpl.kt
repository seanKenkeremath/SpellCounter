package com.kenkeremath.mtgcounter.persistence

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.ui.setup.theme.SpellCounterTheme

class DatastoreImpl(context: Context) : Datastore {

    companion object {

        private const val PREFERENCES_NAME = "com.kenkeremath.sean"

        private const val KEY_STARTING_LIFE = "key_starting_life"
        private const val KEY_NUMBER_PLAYERS = "key_number_Players"
        private const val KEY_KEEP_SCREEN_ON = "key_keep_screen_on"
        private const val KEY_HIDE_NAVIGATION = "key_hide_navigation"
        private const val KEY_TABLETOP_TYPE = "key_tabletop_type"
        //NOTE: this needs to be different than the legacy theme which was "key_theme"
        private const val KEY_THEME = "key_v3_theme"

        private const val KEY_DATASTORE_VERSION = "key_datastore_version"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Activity.MODE_PRIVATE
    )

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
    override var theme: SpellCounterTheme
        get() = SpellCounterTheme.fromId(prefs.getLong(KEY_THEME, SpellCounterTheme.NOT_SET.id))
        set(value) = getEditor().putLong(KEY_THEME, value.id).apply()

    override val version: Int
        get() = prefs.getInt(KEY_DATASTORE_VERSION, 0)

    override fun updateVersion() {
        getEditor().putInt(KEY_DATASTORE_VERSION, Datastore.CURRENT_VERSION).apply()
    }
}