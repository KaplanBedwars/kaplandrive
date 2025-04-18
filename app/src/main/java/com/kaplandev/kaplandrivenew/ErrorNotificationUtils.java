package com.kaplandev.kaplandrivenew;

import android.Manifest;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.IconCompat;



import java.util.concurrent.atomic.AtomicInteger;

public class ErrorNotificationUtils {

    private static final String ERROR_CHANNEL_ID = "kapland_error";
    private static final String ERROR_CHANNEL_NAME = "KaplanDrive Hata Bildirimleri";
    private static final AtomicInteger notificationId = new AtomicInteger(1000);
    private static Context appContext; // Global context

    // 🔹 Context'i kaydetmek için çağırılacak metod
    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
        createErrorNotificationChannel();
    }

    // 🔹 Bildirim kanalını oluştur
    private static void createErrorNotificationChannel() {
        if (appContext == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        NotificationManager manager = appContext.getSystemService(NotificationManager.class);
        if (manager == null) return;

        NotificationChannel channel = new NotificationChannel(
                ERROR_CHANNEL_ID, ERROR_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("KaplanDrive hata bildirim kanalı");
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{0, 800, 400, 800});
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channel.enableLights(true);
        channel.setLightColor(android.graphics.Color.RED);

        manager.createNotificationChannel(channel);
    }

    // 🔹 Bildirim izni kontrolü
    private static boolean isNotificationPermissionGranted() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ActivityCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }

    // 🔹 Kullanıcıdan izin isteme (Eğer izni yoksa)
    private static void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return;

        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, appContext.getPackageName());
        appContext.startActivity(intent);
    }

    // 🔥 **Direkt çağrılabilir hata bildirimi**
    public static void showErrorNotification(String errorTitle, String errorMessage) {
        if (appContext == null || !superman.areErrorNotificationsEnabled(appContext)) {
            return; // Eğer kapalıysa veya initialize edilmemişse işlem yapma
        }
        if (!isNotificationPermissionGranted()) {
            requestNotificationPermission();
            return;
        }

        NotificationManager manager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return;

        PendingIntent pendingIntent = PendingIntent.getActivity(
                appContext, 0, new Intent(appContext, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext, ERROR_CHANNEL_ID)
                .setSmallIcon(R.drawable.error)
                .setContentTitle("❌ " + errorTitle)
                .setContentText(errorMessage)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ERROR)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setColor(android.graphics.Color.RED)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(errorMessage))
                .setVibrate(new long[]{0, 800, 400, 800})
                .setLights(android.graphics.Color.RED, 1000, 2000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            builder.setBubbleMetadata(new NotificationCompat.BubbleMetadata.Builder(
                    pendingIntent, IconCompat.createWithResource(appContext, R.drawable.error))
                    .setAutoExpandBubble(true)
                    .setSuppressNotification(false)
                    .setDesiredHeight(600)
                    .build());
        }

        manager.notify(notificationId.incrementAndGet(), builder.build());
    }
}
