package com.kaplandev.kaplandrivenew;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        // ActionBar'ı gizle
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ImageView logo = findViewById(R.id.splash_logo);
        TextView text = findViewById(R.id.splash_text);

        // Logo animasyonu
        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_fade_in);
        logo.startAnimation(logoAnimation);

        // Yazı animasyonu
        Animation textAnimation = AnimationUtils.loadAnimation(this, R.anim.text_slide_up);
        text.startAnimation(textAnimation);

        // 1 saniye sonra ana ekrana geç
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 1000);
    }
}