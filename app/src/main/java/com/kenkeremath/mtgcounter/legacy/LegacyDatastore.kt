package com.kenkeremath.mtgcounter.legacy

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import com.kenkeremath.mtgcounter.legacy.model.LegacyPlayerTemplateModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.IOException

class LegacyDatastore(context: Context, private val moshi: Moshi) {

    companion object {
        private const val PREFERENCES_NAME = "com.kenkeremath.sean"
        private const val KEY_LEGACY_GAME_STATE = "key_game_state"

        // Not necessary anymore, can just use DS Version
        private const val KEY_LEGACY_FIRST_LAUNCH = "key_first_launch"

        //There was a time before stock game templates existed
        private const val KEY_LEGACY_STOCK_GAME_TEMPLATES_SET = "key_stock_game_templates"
        private const val KEY_LEGACY_PLAYER_TEMPLATES = "key_templates"
        private const val KEY_LEGACY_GAME_TEMPLATES = "key_game_templates"
        private const val KEY_LEGACY_THEME = "key_theme"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Activity.MODE_PRIVATE
    )

    private fun getEditor(): SharedPreferences.Editor {
        return prefs.edit()
    }

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
    val legacyPlayerTemplates: List<LegacyPlayerTemplateModel>
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

}