package com.kaplandev.kaplandrivenew;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.kaplandev.kaplandrivenew.MainActivity;
import com.kaplandev.kaplandrivenew.R;

public class NotificationUtils {

    public static final String CHANNEL_ID = "kapland";
    public static final String CHANNEL_NAME = "KaplanDrive";

    // Bildirim kanalını oluşturma
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    // Bildirim iznini kontrol et
    public static boolean isNotificationPermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            return notificationManager != null && notificationManager.areNotificationsEnabled();
        } else {
            // Android 13 ve öncesinde izinler varsayılan olarak verildiği için burada ek bir kontrol gerekmez
            return true;
        }
    }

    // Bildirim izni verilmediyse, kullanıcıyı izin sayfasına yönlendir
    public static void requestNotificationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 ve sonrası için bildirim izin sayfasına git
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Bildirim izni zaten verilmiş durumda.", Toast.LENGTH_SHORT).show();
        }
    }

    // Bildirim gösterme
    public static void showNotification(Context context, String title, String message) {
        // İzin kontrolü
        if (!isNotificationPermissionGranted(context)) {
            // İzin verilmemişse kullanıcıyı izin sayfasına yönlendir
            requestNotificationPermission(context);
            return;
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)  // Bildirim simgesini değiştir
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        if (manager != null) {
            manager.notify(1, notification);
        }
    }
}
