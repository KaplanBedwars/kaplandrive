package com.kaplandev.kaplandrivenew;

import android.graphics.drawable.Animatable;
import android.opengl.GLES10;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import javax.microedition.khronos.opengles.GL10;

public class UnsupportedVersionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unsupported_version);
        DisplayHelper.hideCameraCutout(this);

        // View tanımlamaları
        ImageView icon = findViewById(R.id.icon);
        Button detailsButton = findViewById(R.id.details_button);
        TextView detailsText = findViewById(R.id.details_text);
        Button exitButton = findViewById(R.id.exit_button);
        TextView deviceSpecs = findViewById(R.id.device_specs);
        TextView warningText = findViewById(R.id.warning_text);

        // Animasyonlu ikon ayarla
        setupAnimatedIcon(icon);

        // Cihaz bilgilerini göster
        displayDeviceSpecs(deviceSpecs);

        // Detay butonu işlevi
        setupDetailsButton(detailsButton, detailsText);

        // Çıkış butonu işlevi
        setupExitButton(exitButton);

        // Sistem uyarısını göster
        showSystemWarning();

        hideActionBar();
    }

    private void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }//

    private void setupAnimatedIcon(ImageView icon) {
        try {
            AnimatedVectorDrawableCompat animatedIcon =
                    AnimatedVectorDrawableCompat.create(this, R.drawable.ic_warning_anim);
            icon.setImageDrawable(animatedIcon);
            if (animatedIcon != null) {
                animatedIcon.start();
                // Sonsuz döngü için animasyon dinleyici
                if (animatedIcon instanceof Animatable) {
                    ((Animatable) animatedIcon).start();
                }
            }
        } catch (Exception e) {
            icon.setImageResource(android.R.drawable.ic_dialog_alert);
            Toast.makeText(this, "Animasyon yüklenemedi", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayDeviceSpecs(TextView deviceSpecs) {
        String specs = String.format(
                "▸ Model: %s\n" +
                        "▸ Android: %s (API %d)\n" +
                        "▸ İşlemci: %s\n" +
                        "▸ RAM: %.1f GB",
                Build.MODEL,
                Build.VERSION.RELEASE,
                Build.VERSION.SDK_INT,
                Build.SUPPORTED_ABIS[0],
                Runtime.getRuntime().maxMemory() / 1073741824.0
        );
        deviceSpecs.setText(specs);
    }

    private void setupDetailsButton(Button detailsButton, TextView detailsText) {
        detailsButton.setOnClickListener(v -> {
            if (detailsText.getVisibility() == View.VISIBLE) {
                hideDetailsPanel(detailsText);
            } else {
                showDetailsPanel(detailsText);
            }
        });

        // İlk açılışta detayları yükle
        detailsText.setText(generateTechnicalDetails());
    }

    private void showDetailsPanel(TextView panel) {
        panel.setVisibility(View.VISIBLE);
        panel.setAlpha(0f);
        panel.setTranslationY(20f);

        panel.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .setInterpolator(new OvershootInterpolator(1.2f))
                .start();
    }

    private void hideDetailsPanel(TextView panel) {
        panel.animate()
                .translationY(10f)
                .alpha(0f)
                .setDuration(400)
                .setInterpolator(new AccelerateInterpolator())
                .withEndAction(() -> panel.setVisibility(View.GONE))
                .start();
    }

    private String generateTechnicalDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("✦ Sistem Detayları:\n\n");
        sb.append("▸ OpenGL: ").append(getGLVersion()).append("\n");
        sb.append("▸ Ekran: ").append(getResources().getDisplayMetrics().widthPixels)
                .append("x").append(getResources().getDisplayMetrics().heightPixels).append("px\n");
        sb.append("▸ Yoğunluk: ").append(getResources().getDisplayMetrics().densityDpi).append("dpi\n");
        sb.append("▸ GPU: ").append(getGPUInfo()).append("\n\n");
        sb.append("✦ Minimum Gereksinimler:\n\n");
        sb.append("▸ Android 12 (API 31)\n");
        sb.append("▸ 4GB RAM\n");
        sb.append("▸ Vulkan 1.1 desteği");

        return sb.toString();
    }

    private String getGLVersion() {
        try {
            return GLES10.glGetString(GLES10.GL_VERSION);
        } catch (Exception e) {
            return "Bilinmiyor";
        }
    }

    private String getGPUInfo() {
        try {
            String renderer = GLES10.glGetString(GLES10.GL_RENDERER);
            String vendor = GLES10.glGetString(GLES10.GL_VENDOR);
            return renderer + " (" + vendor + ")";
        } catch (Exception e) {
            return "Bilinmiyor";
        }
    }

    private void setupExitButton(Button exitButton) {
        exitButton.setOnClickListener(v -> {
            // Buton animasyonu
            v.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        // Çıkış işlemleri
                        finishAffinity();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            finishAndRemoveTask();
                        }
                        System.exit(0);
                    })
                    .start();
        });
    }

    private void showSystemWarning() {
        Toast.makeText(this,
                "⚠ Cihaz uyumsuzluğu tespit edildi",
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Animasyonları temizle
        ImageView icon = findViewById(R.id.icon);
        if (icon.getDrawable() instanceof Animatable) {
            ((Animatable) icon.getDrawable()).stop();
        }
    }
}