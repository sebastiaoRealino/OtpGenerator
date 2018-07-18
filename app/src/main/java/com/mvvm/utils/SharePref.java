package com.mvvm.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.mvvm.config.Constants;

public class SharePref {
    private static SharePref sharePref = new SharePref();
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;


    private SharePref() {} //prevent creating multiple instances by making the constructor private

    public static SharePref getInstance(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
        return sharePref;
    }

    public void saveKey(String key) {
        editor.putString(Constants.PREFS_NAME, key);
        editor.commit();
    }

    public String getAnyKey() {
        return sharedPreferences.getString(Constants.PREFS_NAME, "");
    }

    public void clearAll() {
        editor.clear();
        editor.commit();
    }

}