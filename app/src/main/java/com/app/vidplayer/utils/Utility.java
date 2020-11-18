package com.app.vidplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.app.vidplayer.R;

public class Utility {
    public static SharedPreferences prefs;
    public static void setTheme(Context context, int theme){
        PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        prefs.edit().putInt(context.getString(R.string.prefs_theme_key), theme).apply();
        editor.apply();
    }
    public static int getTheme(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return  prefs.getInt(context.getString(R.string.prefs_theme_key), -1);
    }

}
