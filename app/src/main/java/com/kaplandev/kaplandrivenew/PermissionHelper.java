package com.kaplandev.kaplandrivenew;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class PermissionHelper {

    private static ActivityResultLauncher<String> permissionLauncher;
    private static AppCompatActivity activityRef;

    public static void checkNotificationPermission(AppCompatActivity activity) {
        activityRef = activity;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                setupPermissionLauncher();
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                proceed();
            }
        } else {
            proceed();
        }
    }

    private static void setupPermissionLauncher() {
        permissionLauncher = activityRef.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result != null && result) {
                        proceed();
                    } else {
                        Toast.makeText(activityRef, "Bildirim izni reddedildi. Bildirimler gösterilmeyecek", Toast.LENGTH_SHORT).show();
                        superman.setErrorNotificationsEnabled(activityRef, false);
                        proceed();
                    }
                });
    }

    private static void proceed() {
        if (activityRef instanceof PermissionCallback) {
            ((PermissionCallback) activityRef).onPermissionCheckFinished();
        }
    }

    public interface PermissionCallback {
        void onPermissionCheckFinished();
    }
}
