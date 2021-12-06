package com.kenkeremath.mtgcounter.persistence

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

import com.kenkeremath.mtgcounter.legacy.model.LegacyPlayerTemplateModel
import com.kenkeremath.mtgcounter.model.TabletopType
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

import java.io.IOException
import java.lang.reflect.Type
import java.util.ArrayList

class DatastoreImpl(context: Context, private val moshi: Moshi) : Datastore {

    companion object {

        private val PREFERENCES_NAME = "com.kenkeremath.sean"

        private val KEY_GAME_STATE = "key_game_state"
        private val KEY_FIRST_LAUNCH = "key_first_launch"
        private val KEY_PLAYER_TEMPLATES = "key_templates"
        private val KEY_GAME_TEMPLATES = "key_game_templates"
        private val KEY_THEME = "key_theme"
        //This is a counter for counters... we are using this for Ids on new counters creating and incrementing each time
        private val KEY_COUNTER_TEMPLATE_COUNTER = "key_counter_template_counter"
        private val KEY_STARTING_LIFE = "key_starting_life"
        private val KEY_NUMBER_PLAYERS = "key_number_Players"
        private val KEY_KEEP_SCREEN_ON = "key_keep_screen_on"
        private val KEY_HIDE_NAVIGATION = "key_hide_navigation"
        private val KEY_TABLETOP_TYPE = "key_tabletop_type"


        private val KEY_DATASTORE_VERSION = "key_datastore_version"
        //1.1 = 0
        //1.5 = 1
        //2.0 = 1 (we added editMode flag but that can default to false)
        //2.1 = 1 (added game templates, but this requires no change)
        //3.0 = 2 (rewrite, lot of changes) //TODO: more detail here
        private val DATASTORE_VERSION = 2
    }

    private val prefs: SharedPreferences

    init {
        prefs = context.getSharedPreferences(
            PREFERENCES_NAME,
            Activity.MODE_PRIVATE
        )
    }

    override val isFirstLaunch: Boolean
        get() = prefs.getBoolean(KEY_FIRST_LAUNCH, true)


    //TODO
    val playerTemplates: ArrayList<LegacyPlayerTemplateModel>
        get() {
            val returnList = ArrayList<LegacyPlayerTemplateModel>()
            val storedJson = prefs.getString(KEY_PLAYER_TEMPLATES, null)
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
        set(value)  = getEditor().putInt(KEY_STARTING_LIFE, value).apply()
    override var numberOfPlayers: Int
        get() = prefs.getInt(KEY_NUMBER_PLAYERS, 1)
        set(value)  = getEditor().putInt(KEY_NUMBER_PLAYERS, value).apply()
    override var keepScreenOn: Boolean
        get() = prefs.getBoolean(KEY_KEEP_SCREEN_ON, true)
        set(value)  = getEditor().putBoolean(KEY_KEEP_SCREEN_ON, value).apply()
    override var hideNavigation: Boolean
        get() = prefs.getBoolean(KEY_HIDE_NAVIGATION, false)
        set(value)  = getEditor().putBoolean(KEY_HIDE_NAVIGATION, value).apply()
    override var tabletopType: TabletopType
        get() = TabletopType.values()[prefs.getInt(KEY_TABLETOP_TYPE, TabletopType.LIST.ordinal)]
        set(value)  = getEditor().putInt(KEY_TABLETOP_TYPE, value.ordinal).apply()

    override fun setFirstLaunchComplete() {
        //Subsequent checks for first launch will return false
        getEditor().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }

    fun upgrade() {
        val prevVersion = prefs.getInt(KEY_DATASTORE_VERSION, 0)

        //TODO: do upgrade

        getEditor().putInt(KEY_DATASTORE_VERSION, DATASTORE_VERSION).apply()
    }

    override fun getNewCounterTemplateId(): Int {
        val currCounter = prefs.getInt(KEY_COUNTER_TEMPLATE_COUNTER, 1)
        getEditor().putInt(KEY_COUNTER_TEMPLATE_COUNTER, currCounter + 1).apply()
        return currCounter
    }



    //    public ArrayList<GameTemplateModel> getGameTemplates() {
    //        ArrayList<GameTemplateModel> returnList = new ArrayList<>();
    //        String storedJson = getPrefs().getString(KEY_GAME_TEMPLATES, null);
    //        if (storedJson != null) {
    //            try {
    //                List<GameTemplateModel> storedList = mGson.fromJson(storedJson, new TypeToken<ArrayList<GameTemplateModel>>(){}.getType());
    //                returnList.addAll(storedList);
    //            } catch (JsonParseException ignored) {}
    //        }
    //        return returnList;
    //    }

    //    public void setPlayerTemplates(List<PlayerTemplateModel> templates) {
    //        String json = mGson.toJson(templates);
    //        getEditor().putString(KEY_PLAYER_TEMPLATES, json).apply();
    //    }

    //    public void setGameTemplates(List<GameTemplateModel> templates) {
    //        String json = mGson.toJson(templates);
    //        getEditor().putString(KEY_GAME_TEMPLATES, json).apply();
    //    }

    //    public void setStockPlayerTemplates(Context context) {
    //        List<PlayerTemplateModel> defaultTemplates = new ArrayList<>();
    //        defaultTemplates.add(PlayerTemplateModel.defaultModernTemplate(context));
    //        defaultTemplates.add(PlayerTemplateModel.defaultStandardTemplate(context));
    //        defaultTemplates.add(PlayerTemplateModel.defaultEdhTemplate(context));
    //        setPlayerTemplates(defaultTemplates);
    //    }
    //
    //    public void setStockGameTemplates(Context context) {
    //        List<GameTemplateModel> defaultTemplates = new ArrayList<>();
    //        defaultTemplates.add(GameTemplateModel.defaultStandardTemplate(context));
    //        defaultTemplates.add(GameTemplateModel.defaultDuelEdhTemplate(context));
    //        defaultTemplates.add(GameTemplateModel.default2EdhTemplate(context));
    //        defaultTemplates.add(GameTemplateModel.default3EdhTemplate(context));
    //        defaultTemplates.add(GameTemplateModel.default4EdhTemplate(context));
    //        setGameTemplates(defaultTemplates);
    //        getEditor().putBoolean(KEY_STOCK_GAME_TEMPLATES_SET, true).apply();
    //    }
    //
    //    public boolean playerTemplateExists(@NonNull String templateName) {
    //        for (PlayerTemplateModel playerTemplateModel : getPlayerTemplates()) {
    //            if (playerTemplateModel.getTemplateName().equals(templateName)) {
    //                return true;
    //            }
    //        }
    //        return false;
    //    }
    //
    //    public boolean gameTemplateExists(@NonNull String templateName) {
    //        for (GameTemplateModel gameTemplateModel : getGameTemplates()) {
    //            if (gameTemplateModel.getTemplateName().equals(templateName)) {
    //                return true;
    //            }
    //        }
    //        return false;
    //    }

    //    /**
    //     * check for duplicates. if duplicate exists, replace that template
    //     * @param template new template to be added
    //     */
    //    public void addPlayerTemplate(PlayerTemplateModel template) {
    //        ArrayList<PlayerTemplateModel> templates = getPlayerTemplates();
    //        int index = -1;
    //        for (int i = 0; i < templates.size(); i++) {
    //            if (templates.get(i).getTemplateName().equals(template.getTemplateName())) {
    //                index = i;
    //            }
    //        }
    //        if (index != -1) {
    //            //replace
    //            templates.set(index, template);
    //        } else {
    //            //no duplicates, just add to end
    //            templates.add(template);
    //        }
    //        setPlayerTemplates(templates);
    //    }
    //
    //    public void addGameTemplate(GameTemplateModel template) {
    //        ArrayList<GameTemplateModel> templates = getGameTemplates();
    //        int index = -1;
    //        for (int i = 0; i < templates.size(); i++) {
    //            if (templates.get(i).getTemplateName().equals(template.getTemplateName())) {
    //                index = i;
    //            }
    //        }
    //        if (index != -1) {
    //            //replace
    //            templates.set(index, template);
    //        } else {
    //            //no duplicates, just add to end
    //            templates.add(template);
    //        }
    //        setGameTemplates(templates);
    //    }


    //    public void deletePlayerTemplate(PlayerTemplateModel template) {
    //        ArrayList<PlayerTemplateModel> templates = getPlayerTemplates();
    //        for (int i = 0; i < templates.size(); i++) {
    //            if (templates.get(i).getTemplateName().equals(template.getTemplateName())) {
    //                templates.remove(i);
    //                setPlayerTemplates(templates);
    //                return;
    //            }
    //        }
    //    }
    //
    //    public void deleteGameTemplate(GameTemplateModel template) {
    //        ArrayList<GameTemplateModel> templates = getGameTemplates();
    //        for (int i = 0; i < templates.size(); i++) {
    //            if (templates.get(i).getTemplateName().equals(template.getTemplateName())) {
    //                templates.remove(i);
    //                setGameTemplates(templates);
    //                return;
    //            }
    //        }
    //    }
    //
    //    public void setTheme(Theme theme) {
    //        getEditor().putString(KEY_THEME, theme.name()).apply();
    //    }
    //
    //    public Theme getTheme() {
    //        try {
    //            return Theme.valueOf(getPrefs().getString(KEY_THEME, Theme.GREY.name()));
    //        } catch (Exception e) {
    //            return Theme.GREY;
    //        }
    //    }
}