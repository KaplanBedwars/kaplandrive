package com.kaplandev.kaplandrivenew;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 1000; // 1 saniye

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        hideActionBar();

        startAnimations();
        navigateToMainAfterDelay();
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

    private void navigateToMainAfterDelay() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, SPLASH_DURATION);
    }
}