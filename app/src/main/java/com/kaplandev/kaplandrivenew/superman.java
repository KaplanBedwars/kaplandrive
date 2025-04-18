package com.kaplandev.kaplandrivenew;


import android.content.Context;
import android.content.SharedPreferences;

//kurtarıcı SUPERMAAAANANANAFHNHJGKFJGFGF

public class superman {
    private static final String PREFS_NAME = "AppSettings";
    private static final String KEY_SERVER_URL = "server_url";
    private static final String KEY_TIPS_ENABLED = "tips_enabled";
    private static final String KEY_UPDATES_ENABLED = "updates_enabled";
    private static final String KEY_sSCREEN_ENABLED = "sscreen_enabled";// DÜZELTİLDİ
    private static final String KEY_ERROR_NOTIFICATIONS = "error_notifications";
    private static final String KEY_NO_ENABLED = "no_enabled";
    private static final String DEFAULT_URL = "http://192.168.1.38:8080";

    // URL İşlemleri
    public static void set(Context context, String url) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_SERVER_URL, url);
        editor.apply();
    }

    public static String get(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_SERVER_URL, DEFAULT_URL);
    }

    // Tips Ayarı
    public static void setTipsEnabled(Context context, boolean enabled) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_TIPS_ENABLED, enabled);
        editor.apply();
    }

    public static boolean isTipsEnabled(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_TIPS_ENABLED, true);
    }

    // Hata Bildirimleri Ayarı
    public static void setErrorNotificationsEnabled(Context context, boolean enabled) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_ERROR_NOTIFICATIONS, enabled);
        editor.apply();
    }

    public static boolean areErrorNotificationsEnabled(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_ERROR_NOTIFICATIONS, true);
    }

    // Updates Ayarı
    public static void setUpdatesEnabled(Context context, boolean enabled) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_UPDATES_ENABLED, enabled);
        editor.apply();
    }

    public static boolean isSscrenEnabled(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_sSCREEN_ENABLED, true);
    }

    public static void setSscreenEnabled(Context context, boolean enabled) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_sSCREEN_ENABLED, enabled);
        editor.apply();
    }

    public static boolean isUpdatesEnabled(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_UPDATES_ENABLED, true);
    }

    public static boolean isnoEnabled(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_NO_ENABLED, true);
    }

    public static void setnoEnabled(Context context, boolean enabled) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_NO_ENABLED, enabled);
        editor.apply();
    }
}


