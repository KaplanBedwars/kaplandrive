package com.kaplandev.kaplandrivenew;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        setContentView(R.layout.splash);
        hideActionBar();
        startAnimations();

        // Android versiyon kontrolü ve izin kontrolünü birleştir
        new Handler().postDelayed(() -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                startActivity(new Intent(this, UnsupportedVersionActivity.class));
                finish();
            } else {
                checkNotificationPermission();
            }
        }, SPLASH_DURATION);
    }

    private void checkNotificationPermission() {
        // Android 13+ (API 33+) için bildirim izni kontrolü
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                // İzin isteği göster
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            } else {
                proceedToMainActivity();
            }
        } else {
            // Android 12 ve altında izin gerekmez
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
                // İzin reddedildi
                Toast.makeText(this, "Bildirim izni olmadan uygulama çalışamaz!", Toast.LENGTH_LONG).show();
                finishAffinity(); // Uygulamayı tamamen kapat
            }
        }
    }

    private void proceedToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void startAnimations() {
        ImageView logo = findViewById(R.id.splash_logo);
        TextView text = findViewById(R.id.splash_text);
        logo.startAnimation(loadAnimation(R.anim.logo_fade_in));
        text.startAnimation(loadAnimation(R.anim.text_slide_up));
    }

    private Animation loadAnimation(int animationResId) {
        return AnimationUtils.loadAnimation(this, animationResId);
    }
}