package com.kaplandev.kaplandrivenew;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class firstRunActiivty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firstrun); // XML dosyanızın adı neyse onu yazın
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        DisplayHelper.hideCameraCutout(this);
        // Butonları tanımla
        MaterialButton btnPrivacyDetails = findViewById(R.id.btnPrivacyDetails);
        MaterialButton btnGithub = findViewById(R.id.btnGithub);
        MaterialButton autoSetupButton = findViewById(R.id.autoSetupButton);
        MaterialButton manualSetupButton = findViewById(R.id.manualSetupButton);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) MaterialButton btnserver = findViewById(R.id.btnserver);

        // Gizlilik Politikası Detay Butonu
        btnPrivacyDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrivacyPolicyDialog();
            }
        });

        // GitHub Butonu
        btnGithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGithubPage();
            }
        });

        // Otomatik Ayarlar Butonu
        autoSetupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyOptimalSettings();
            }
        });

        // Manuel Ayarlar Butonu
        manualSetupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openManualSettings();
            }
        });

        btnserver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               server();
            }
        });
    }

    private void showPrivacyPolicyDialog() {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/KaplanBedwars/kaplandrive/blob/main/TERMS.md"));
            startActivity(browserIntent);
        } catch (Exception e) {
            cow.show(this, "sayfa açılamadı");
        }
    }

    private void openGithubPage() {
        try {
            Intent browserIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/KaplanBedwars/KaplanDrive")
            );
            startActivity(browserIntent);
        } catch (Exception e) {
            Toast.makeText(this, "GitHub sayfası açılamadı", Toast.LENGTH_SHORT).show();
        }
    }

    private void applyOptimalSettings() {
       superman.setErrorNotificationsEnabled(this, false);
       superman.setnoEnabled(this, false);
       cow.show(this, "Ayarlar optimize edildi.");
       finish();
    }

    private void openManualSettings() {
        finish();
    }
    private  void server(){

        try {
            Intent browserIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/KaplanBedwars/KaplanDrive")
            );
            startActivity(browserIntent);
        } catch (Exception e) {
            Toast.makeText(this, "GitHub sayfası açılamadı", Toast.LENGTH_SHORT).show();
        }
    }
}