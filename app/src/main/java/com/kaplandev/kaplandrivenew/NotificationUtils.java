package com.kaplandev.kaplandrivenew;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;
import androidx.core.graphics.drawable.IconCompat;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.kaplandev.kaplandrivenew.MainActivity;
import com.kaplandev.kaplandrivenew.R;

import java.util.concurrent.atomic.AtomicInteger;

public class NotificationUtils {

    public static final String CHANNEL_ID = "kapland";
    public static final String CHANNEL_NAME = "KaplanDrive";

    private static final AtomicInteger notificationId = new AtomicInteger(0); // Her bildirim için benzersiz ID

    // Bildirim kanalını oluşturma
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH // Önem düzeyini yüksek yapın
            );
            channel.setDescription("KaplanDrive bildirim kanalı");
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC); // Kilit ekranında göster
            channel.enableVibration(true); // Titreşim etkinleştir
            channel.setVibrationPattern(new long[]{0, 500, 250, 500}); // Titreşim deseni

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    //Baloncuk iznini konstro et:


    // Bildirim iznini kontrol et (Android 13+ için)
    public static boolean isNotificationPermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true; // Android 12 ve öncesinde izin gerekmiyor
        }


    }

    // Bildirim izni verilmediyse, kullanıcıyı izin sayfasına yönlendir
    public static void requestNotificationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
            requestNotificationPermission(context);
            return;
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return;

        // MainActivity'yi açacak intent
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE // FLAG_MUTABLE ekleyin
        );

        // Bildirim oluştur
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)  // Bildirim simgesi
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Öncelikli bildirim
                .setCategory(NotificationCompat.CATEGORY_MESSAGE) // Kategori belirle
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC); // Bildirimi her zaman göster

        // Android 11+ Açılır Pencere Bildirimi Desteği
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // BubbleMetadata için bir IconCompat ekleyin
            IconCompat bubbleIcon = IconCompat.createWithResource(context, R.drawable.logo); // logo.png veya başka bir ikon

            // BubbleMetadata oluştur
            NotificationCompat.BubbleMetadata bubbleMetadata = new NotificationCompat.BubbleMetadata.Builder()
                    .setIcon(bubbleIcon) // IconCompat kullanın
                    .setIntent(pendingIntent) // Balon tıklandığında açılacak intent
                    .setAutoExpandBubble(true) // Balonun otomatik genişlemesi
                    .setDesiredHeight(600) // Balonun yüksekliği (isteğe bağlı)
                    .build();

            builder.setBubbleMetadata(bubbleMetadata);
        }

        // Bildirimi göster
        manager.notify(notificationId.incrementAndGet(), builder.build());
    }

}