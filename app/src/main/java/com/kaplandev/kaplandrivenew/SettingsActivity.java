package com.kaplandev.kaplandrivenew;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        hideActionBar();

        // View'leri tanımla
        editTextServerUrl = findViewById(R.id.editTextServerUrl);
        switchTips = findViewById(R.id.switchTips);  // 🔴 Yerel değişken TANIMLAMADAN kullan!
        switchErrorNotifications = findViewById(R.id.switchErrorNotifications);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        Button buttonSave = findViewById(R.id.buttonSaveSettings);
        Button doc = findViewById(R.id.docs);
        View btnCheckUpdate2 = findViewById(R.id.buttonCheckUpdates);

        btnCheckUpdate2.setOnClickListener(v -> checkForUpdate());
        doc.setOnClickListener(v -> docs());

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
    }
    private void docs() {
        startActivity(new Intent(this, doc.class));
    }
    private void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
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

        showSuccess("Ayarlar kaydedildi!");
        finish();
    }

    // Yardımcı metodlar
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        editTextServerUrl.setError(message);
        editTextServerUrl.requestFocus();
    }

    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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

                    if (!currentVersion.equals(latestVersion)) {
                        runOnUiThread(() -> showUpdateSnackbar(findViewById(android.R.id.content), CURTESTV));

                    } else {
                        runOnUiThread(() ->   Toast.makeText(this, "Sürümünüz güncel", Toast.LENGTH_SHORT).show());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->   Toast.makeText(this, "İnternet bağlantınızı kontrol edin", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    public void showUpdateSnackbar(View view, String latestVersion) {
        if (view == null) return;

        // Snackbar oluştur
        Snackbar snackbar = Snackbar.make(view, "📢 Yeni sürüm (" + latestVersion + ") mevcut. İndirmek ister misiniz?", Snackbar.LENGTH_INDEFINITE);

        // Snackbar görünümünü al
        View snackbarView = snackbar.getView();
        snackbarView.setPadding(40, 30, 40, 30); // Kenar boşlukları artır
        snackbarView.setMinimumHeight(200); // Yüksekliği artır

        // Snackbar'daki metni büyüt
        TextView snackbarText = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        snackbarText.setTextSize(18);
        snackbarText.setGravity(Gravity.CENTER_VERTICAL);
        snackbarText.setMaxLines(3);

        // Karanlık/Açık tema uyarlaması
        int nightModeFlags = view.getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            snackbarView.setBackgroundColor(Color.DKGRAY);
            snackbarText.setTextColor(Color.WHITE);
        } else {
            snackbarView.setBackgroundColor(Color.WHITE);
            snackbarText.setTextColor(Color.BLACK);
        }

        // "İndir" butonunu ekle ve MAVİ renkte yap
        snackbar.setAction("📥 İndir", v -> downloadUpdate());
        snackbar.setActionTextColor(Color.BLUE); // MAVİ Renk

        // Snackbar'ın otomatik kapanma süresini ayarla (örneğin 10 saniye)
        snackbar.setDuration(10000); // 10 saniye sonra otomatik kapanır

        snackbar.show(); // Snackbar'ı göster
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