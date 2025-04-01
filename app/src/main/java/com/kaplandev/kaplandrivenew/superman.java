package com.kaplandev.kaplandrivenew;


import android.content.Context;
import android.content.SharedPreferences;

//kurtarıcı SUPERMAAAANANANAFHNHJGKFJGFGF
public class superman {
    private static final String PREFS_NAME = "MyAppSettings";
    private static final String KEY_SERVER_URL = "server_url";
    private static String defaultUrl = "http://192.168.1.38:8080";

    public static void set(Context context, String newUrl) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_SERVER_URL, newUrl).apply();
    }

    public static String get(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_SERVER_URL, defaultUrl);
    }
}
