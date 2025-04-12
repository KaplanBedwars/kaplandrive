package com.kaplandev.kaplandrivenew;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class update extends AppCompatActivity {

    private LinearProgressIndicator progressIndicator;
    private MaterialButton btnAutoUpdate;
    private MaterialButton btnManualDownload;
    private boolean isDownloading = false;
    private int downloadProgress = 0;
    private final Handler progressHandler = new Handler();

    private static String UPDATE_URL;
    private static String APK_DOWNLOAD_URL;
    private static String CURRENT_VERSION;
    private static String CURTESTV;
    public static void init(Context context) {
        UPDATE_URL = context.getString(R.string.update_url);
        APK_DOWNLOAD_URL = context.getString(R.string.apk_download_url);
        CURRENT_VERSION = context.getString(R.string.current_version);
        CURTESTV = context.getString(R.string.current_test_version);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update); // XML dosyanızın adı bu olmalı
        init(getApplicationContext());
        // Toolbar ayarları
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Bileşenleri bağlama
        progressIndicator = findViewById(R.id.updateProgress2);
        btnAutoUpdate = findViewById(R.id.btnAutoUpdate);
        btnManualDownload = findViewById(R.id.btnManualDownload);

        // Buton click listener'ları
        btnAutoUpdate.setOnClickListener(v -> startAutoUpdate());
        btnManualDownload.setOnClickListener(v -> openGithubPage());

        // Geri butonu işlemi
        findViewById(R.id.toolbar).setOnClickListener(v -> finish());
    }

    private void startAutoUpdate() {
        downloadUpdate();

    }



    private void openGithubPage() {
        // GitHub sayfasını açma işlemi
        Toast.makeText(this, "GitHub sayfasına yönlendiriliyor...", Toast.LENGTH_SHORT).show();


        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://github.com/KaplanBedwars/kaplandrive/releases"));
            startActivity(browserIntent);
        } catch (Exception e) {
            Toast.makeText(this, "GitHub sayfası açılamadı", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Handler'ı temizle
        progressHandler.removeCallbacksAndMessages(null);
    }

    private void downloadUpdate() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(APK_DOWNLOAD_URL))
                .setTitle("KaplanDrive Güncelleme")
                .setDescription("Yeni sürüm indiriliyor...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "KaplanDrive.apk");

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

        tips.show(findViewById(android.R.id.content), "Bilgi!", "Yeni sürüm indiriliyor!");
        //tips.show(findViewById(android.R.id.content), "", "");
    }
}