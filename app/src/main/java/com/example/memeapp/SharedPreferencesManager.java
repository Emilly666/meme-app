package com.example.memeapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.memeapp.model.tag.Tag;
import com.example.memeapp.model.user.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesManager {
    private static final String APP_PREFS = "AppPrefsFile";
    private static final List<Tag> userSavedTags = new ArrayList<>();
    private static final String SERVER_ADDRESS = "http://192.168.100.32:8080/";
    private static final String token = "";
    private static final User user = new User();

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
    public List<Tag> getUserSavedTags() {
        Gson gson = new Gson();
        return gson.fromJson(sp.getString("tags", null), new TypeToken<ArrayList<Tag>>() {}.getType());
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

    public String getServerAddress(){
        return SERVER_ADDRESS;
    }
    public String getToken(){
        return sp.getString("token", null);
    }
    public void setToken(String token){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("token", token);
        editor.apply();
    }
    public User getUser(){
        Gson gson = new Gson();
        return gson.fromJson(sp.getString("user", null), new TypeToken<User>() {}.getType());
    }
    public void setUser(User user) {
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();

        String jsonText = gson.toJson(user);
        editor.putString("user", jsonText);
        editor.apply();
    }
}
