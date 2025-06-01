package com.kaplandev.kaplandrivenew.crashHandlers;

import android.app.Application;

public class setup extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Burada crash handler kurulumu yapılır
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
    }
}
