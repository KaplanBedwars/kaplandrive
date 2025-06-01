package com.kaplandev.kaplandrivenew;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



public class SettingsActivity extends AppCompatActivity {

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

    private EditText editTextServerUrl;
    private SwitchMaterial switchTips, switchErrorNotifications;
    private SwitchMaterial switchupdate;
    private SwitchMaterial switchsscreen;
    private SwitchMaterial switchno;
    private SwitchMaterial bm;


    //@SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        hideActionBar();
        DisplayHelper.hideCameraCutout(this);


        // View'leri tanımla
        editTextServerUrl = findViewById(R.id.editTextServerUrl);
        switchTips = findViewById(R.id.switchTips);  // 🔴 Yerel değişken TANIMLAMADAN kullan!
        switchErrorNotifications = findViewById(R.id.switchErrorNotifications);
        switchupdate = findViewById(R.id.switchupdate);
        switchsscreen = findViewById(R.id.switchsplashscreen);
        switchno = findViewById(R.id.tospik);
        bm = findViewById(R.id.bm);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());
        TextView versionTextView = findViewById(R.id.versionTextView);
        versionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tıklandığında çalışacak kod buraya
                // Örneğin:
                startActivity(new Intent(SettingsActivity.this, info.class));

                // Veya başka bir işlem:
                // startActivity(new Intent(MainActivity.this, OtherActivity.class));
            }
        });



        Button buttonSave = findViewById(R.id.buttonSaveSettings);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button doc = findViewById(R.id.doc);
        Button crash = findViewById(R.id.crash);
        View btnCheckUpdate2 = findViewById(R.id.buttonCheckUpdates);

        btnCheckUpdate2.setOnClickListener(v -> checkForUpdate());
        doc.setOnClickListener(v -> docs());
        crash.setOnClickListener(v -> confirmAndCrash(this));
        // Mevcut ayarları yükle
        loadCurrentSettings();

        // Kaydet butonu
        buttonSave.setOnClickListener(v -> saveSettings());
    }


    private void loadCurrentSettings() {
        // Mevcut URL'yi göster
        editTextServerUrl.setText(superman.get(this));

        // Diğer ayarları yükle
        switchTips.setChecked(superman.isTipsEnabled(this));
        switchErrorNotifications.setChecked(superman.areErrorNotificationsEnabled(this));
        switchupdate.setChecked(superman.isUpdatesEnabled(this));
        switchsscreen.setChecked(superman.isSscrenEnabled(this));
        switchno.setChecked(superman.isnoEnabled(this));
        bm.setChecked(superman.isbmEnabled(this));
    }
    private void docs() {
        startActivity(new Intent(this, doc.class));
    }
    private void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void confirmAndCrash(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Test: Uygulamayı Çökert")
                .setMessage("Uygulamayı çökertmek isityormusunuz? BU İŞLEM GERİ ALINAMAZ")
                .setPositiveButton("Evet", (dialog, which) -> {
                    throw new RuntimeException("TestCrash");
                })
                .setNegativeButton("Hayır", null)
                .show();
    }

    private void saveSettings() {
        String newUrl = editTextServerUrl.getText().toString().trim();

        // 1. Boş kontrolü
        if (newUrl.isEmpty()) {
            showError("URL boş olamaz!");
            return;
        }

        // 2. Geçerli URL formatı kontrolü (Regex ile)
        String urlRegex = "^(https?://)?([a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*|\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:[0-9]{1,5})?(/.*)?$";

        if (!newUrl.matches(urlRegex)) {
            showError("Geçersiz URL formatı!\nÖrnek: http://sunucu.com:8080");
            return;
        }

        // 3. Protokol ekleme (http:// yoksa ekler)
        if (!newUrl.startsWith("http://") && !newUrl.startsWith("https://")) {
            newUrl = "http://" + newUrl; // Varsayılan olarak HTTP
        }

        // Tüm kontroller passed ✅
        superman.set(this, newUrl);
        superman.setTipsEnabled(this, switchTips.isChecked());
        superman.setErrorNotificationsEnabled(this, switchErrorNotifications.isChecked());
        superman.setUpdatesEnabled(this, switchupdate.isChecked());
        superman.setSscreenEnabled(this, switchsscreen.isChecked());
        superman.setnoEnabled(this, switchno.isChecked());
        superman.setbmEnabled(this, bm.isChecked());
        showSuccess("Ayarlar kaydedildi!");
        finish();
    }

    // Yardımcı metodlar
    private void showError(String message) {
        cow.show(this, message);
        editTextServerUrl.setError(message);
        editTextServerUrl.requestFocus();
    }

    private void showSuccess(String message) {
        cow.show(this, message);
    }

    private void checkForUpdate() {
        new Thread(() -> {
            try {
                URL url = new URL(UPDATE_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
                connection.setRequestProperty("User-Agent", "KaplanDrive-App");
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String latestVersion = jsonResponse.getString("tag_name").replace("kaplandrive", "");

                    String currentVersion = CURRENT_VERSION;

                    if (!superman.isUpdatesEnabled(this)) {
                        // Güncellemeler kapalıysa hiçbir şey yapma
                        return;
                    }

                    if (!currentVersion.equals(latestVersion)) {
                        runOnUiThread(() -> startActivity(new Intent(this, update.class)));
                    } else {
                        runOnUiThread(() -> showSuccess("HERŞEY GÜNCEL! YEHUUUUUUUUU"));
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                if (!superman.isUpdatesEnabled(this)) {
                    // Güncellemeler kapalıysa hiçbir şey yapma
                    return;
                }
                runOnUiThread(() -> showSuccess("İnternet bağlantınızı kontrol edin"));
            }
        }).start();
    }




}