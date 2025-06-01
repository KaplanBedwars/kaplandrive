package com.kaplandev.kaplandrivenew.crashHandlers;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private final Context context;

    public CrashHandler(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        // Hata mesajını oluştur
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String errorLog = sw.toString();

        // Downloads klasörüne log dosyasını yaz
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }

            File logFile = new File(downloadsDir, "crash_log.txt");
            FileOutputStream fos = new FileOutputStream(logFile, false); // Üzerine yaz
            fos.write(errorLog.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace(); // Kendi içinde bile crash olmasın
        }

        Intent intent = new Intent(context, CrashDisplayActivity.class);
        intent.putExtra("crash_log", errorLog);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);


        // Biraz zaman ver
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Uygulamayı tamamen kapat
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
