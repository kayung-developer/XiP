package com.app.vidplayer.utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.app.vidplayer.video.VideoModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class StorageUtils {

    private final String STORAGE = "com.app.audiolite.STORAGE";
    private SharedPreferences preferences;
    private Context context;

    public StorageUtils(Context context) {
        this.context = context;
    }

    public void storeAudio(ArrayList<VideoModel> videoArrayList) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(videoArrayList);
        editor.putString("videoArrayList", json);
        editor.apply();
    }

    public ArrayList<VideoModel> videoArraylist() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("videoArrayList", null);
        Type type = new TypeToken<ArrayList<VideoModel>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void storeVideoIndex(int intvid) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("video_index", intvid);
        editor.apply();
    }
    public void storeProgress(int current_pos){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("current_pos", current_pos);
        editor.apply();
    }


    public void clearCachedVideoPlaylist() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}