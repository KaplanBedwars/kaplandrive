package com.kaplandev.kaplandrivenew;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;



public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 1000;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash); // splash.xml layout'unuzun doğru olduğundan emin olun
        hideActionBar();
        DisplayHelper.hideCameraCutout(this);



        // View'ları doğru ID'lerle bağlıyoruz
        ImageView logo = findViewById(R.id.splash_logo);
        TextView versionText = findViewById(R.id.splash_text);

        // Null kontrolü ekliyoruz
        if (logo != null && versionText != null) {
            startAnimations(logo, versionText);
        } else {
            // Hata durumunda direkt ana ekrana geç
            proceedToMainActivity();
            return;
        }
        if (!superman.isSscrenEnabled(this)) {
            PermissionHelper.checkNotificationPermission(this);
            proceedToMainActivity();
            return;
        }




        new Handler().postDelayed(() -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                startActivity(new Intent(this, UnsupportedVersionActivity.class));
                finish();
            } else {
                checkNotificationPermission();
            }
        }, SPLASH_DURATION);
    }

    private void startAnimations(ImageView logo, TextView text) {
        try {
            Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_fade_in);
            Animation textAnimation = AnimationUtils.loadAnimation(this, R.anim.text_slide_up);

            logo.startAnimation(logoAnimation);
            text.startAnimation(textAnimation);
        } catch (Exception e) {
            e.printStackTrace();
            proceedToMainActivity();
        }
    }

    private void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            } else {
                proceedToMainActivity();
            }
        } else {
            proceedToMainActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                proceedToMainActivity();
            } else {
                cow.show(this, "Bildirim izni reddedildi. Bildirimler gösterilmeyecek");
                superman.setErrorNotificationsEnabled(this, false);
                proceedToMainActivity(); // Yine de devam et
            }
        }
    }










    private void proceedToMainActivity() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String className = prefs.getString("boot_class", null); // kullanıcı daha önce bir şey seçti mi?

        Class<?> activityClass = MainActivity.class; // varsayılan

        if (className != null) {
            try {
                activityClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("ERROR_BOOTCLASS_NOT_FOUND");
            }
        }

        startActivity(new Intent(this, activityClass));
        finish();
    }

}