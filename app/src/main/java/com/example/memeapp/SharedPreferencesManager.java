package com.example.memeapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.memeapp.model.Tag.Tag;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesManager {
    private static final String APP_PREFS = "AppPrefsFile";
    private static final List<Tag> userSavedTags = new ArrayList<>();

    private SharedPreferences sp;
    private static SharedPreferencesManager instance;

    private SharedPreferencesManager(Context context) {
        sp = context.getApplicationContext().getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
    }
    public static synchronized SharedPreferencesManager getInstance(Context context){
        if(instance == null)
            instance = new SharedPreferencesManager(context);

        return instance;
    }

    public void addUserSavedTags(Tag tag) {
        SharedPreferences.Editor editor = sp.edit();

        Gson gson = new Gson();
        String jsonText = sp.getString("tags", gson.toJson(userSavedTags));
        List<Tag> tags = gson.fromJson(jsonText, ArrayList.class);

        tags.add(tag);

        jsonText = gson.toJson(tags);
        editor.putString("tags", jsonText);
        editor.apply();
    }
    public List<Tag> getUserSavedTags() {
        Gson gson = new Gson();


        return gson.fromJson(sp.getString("tags", null), new TypeToken<ArrayList<Tag>>() {}.getType());
    }
}
