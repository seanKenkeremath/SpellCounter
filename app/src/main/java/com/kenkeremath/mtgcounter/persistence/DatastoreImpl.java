package com.kenkeremath.mtgcounter.persistence;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.kenkeremath.mtgcounter.legacy.model.LegacyPlayerTemplateModel;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DatastoreImpl implements Datastore {

    //TODO: make kotlin

    private static final String PREFERENCES_NAME = "com.kenkeremath.sean";

    private static final String KEY_GAME_STATE = "key_game_state";
    private static final String KEY_FIRST_LAUNCH = "key_first_launch";
    //If someone has legacy version before game templates, give them the stock game templates
    private static final String KEY_STOCK_GAME_TEMPLATES_SET = "key_stock_game_templates";
    private static final String KEY_PLAYER_TEMPLATES = "key_templates";
    private static final String KEY_GAME_TEMPLATES = "key_game_templates";
    private static final String KEY_THEME = "key_theme";
    //This is a counter for counters... we are using this for Ids on new counters creating and incrementing each time
    private static final String KEY_COUNTER_TEMPLATE_COUNTER = "key_counter_template_counter";

    private static final String KEY_DATASTORE_VERSION = "key_datastore_version";


    //1.1 = 0
    //1.5 = 1
    //2.0 = 1 (we added editMode flag but that can default to false)
    //2.1 = 1 (added game templates, but this requires no change)
    private static int DATASTORE_VERSION = 1;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Moshi moshi;

    public DatastoreImpl(Context context, Moshi moshi) {
        prefs = context.getSharedPreferences(PREFERENCES_NAME,
                Activity.MODE_PRIVATE);
        this.moshi = moshi;
    }

    private SharedPreferences.Editor getEditor() {
        if (editor == null) {
            editor = prefs.edit();
        }
        return editor;
    }

    private SharedPreferences getPrefs() {
        return prefs;
    }

    public boolean isFirstLaunch() {
        return getPrefs().getBoolean(KEY_FIRST_LAUNCH, true);
    }

//    public boolean wereStockGameTemplatesSet() {
//        return getPrefs().getBoolean(KEY_STOCK_GAME_TEMPLATES_SET, false);
//    }

    public void setFirstLaunchComplete() {
        //Subsequent checks for first launch will return false
        getEditor().putBoolean(KEY_FIRST_LAUNCH, false).apply();
    }

    public void upgrade() {
        int prevVersion = getPrefs().getInt(KEY_DATASTORE_VERSION, 0);

        //TODO: do upgrade

        getEditor().putInt(KEY_DATASTORE_VERSION, DATASTORE_VERSION).apply();
    }



    public ArrayList<LegacyPlayerTemplateModel> getPlayerTemplates() {
        //TODO
        ArrayList<LegacyPlayerTemplateModel> returnList = new ArrayList<>();
        String storedJson = getPrefs().getString(KEY_PLAYER_TEMPLATES, null);
        if (storedJson != null) {
            try {
                Type type = Types.newParameterizedType(List.class, LegacyPlayerTemplateModel.class);
                JsonAdapter<List<LegacyPlayerTemplateModel>> adapter = moshi.adapter(type);
                List<LegacyPlayerTemplateModel> storedList = adapter.fromJson(storedJson);
                if (storedList != null) {
                    returnList.addAll(storedList);
                }
            } catch (IOException ignored) {
            }
        }
        return returnList;
    }

    @Override
    public int getNewCounterTemplateId() {
        int currCounter = getPrefs().getInt(KEY_COUNTER_TEMPLATE_COUNTER, 1);
        getEditor().putInt(KEY_COUNTER_TEMPLATE_COUNTER, currCounter + 1).apply();
        return currCounter;
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