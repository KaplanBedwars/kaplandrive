package com.kaplandev.kaplandrivenew;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info); // XML dosyanızın adı burada olmalı
        DisplayHelper.hideCameraCutout(this);

        // GitHub Butonu İşlevi
        MaterialButton githubButton = findViewById(R.id.github_button);
        githubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // GitHub URL'sini aç
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/KaplanBedwars/kaplandrive"));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    Toast.makeText(info.this, "GitHub sayfası açılamadı", Toast.LENGTH_SHORT).show();
                }

                // Butona tıklama animasyonu

            }
        });


        // Hata Bildirimi Butonu İşlevi
        MaterialButton reportButton = findViewById(R.id.report_button);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/KaplanBedwars/kaplandrive/issues/new"));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    Toast.makeText(info.this, "sayfa açılamadı", Toast.LENGTH_SHORT).show();
                }

            }




        });

}}

