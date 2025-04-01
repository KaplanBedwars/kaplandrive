package com.kaplandev.kaplandrivenew;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private EditText editTextServerUrl;
    private superman superman; // Superman sınıfını kullanacağız

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Superman'i başlat
        superman = new superman();

        // View'leri tanımla
        editTextServerUrl = findViewById(R.id.editTextServerUrl);
        Button buttonSave = findViewById(R.id.buttonSaveSettings);

        // Mevcut URL'yi göster
        editTextServerUrl.setText(superman.get(this));

        // Kaydet butonuna tıklanınca
        buttonSave.setOnClickListener(v -> saveSettings());
    }

    private void saveSettings() {
        String newUrl = editTextServerUrl.getText().toString().trim();

        // URL kontrolü
        if (newUrl.isEmpty()) {
            Toast.makeText(this, "URL boş olamaz!", Toast.LENGTH_SHORT).show();
            return;
        }

        // http/https kontrolü
        if (!newUrl.startsWith("http://") && !newUrl.startsWith("https://")) {
            newUrl = "https://" + newUrl;
        }

        // URL'yi güncelle
        superman.set(this, newUrl);
        Toast.makeText(this, "Ayarlar kaydedildi!", Toast.LENGTH_SHORT).show();

        // MainActivity'deki Retrofit'i güncellemek için geri dönüş yapabilirsiniz
        // Örneğin: Intent ile veri gönderme veya onActivityResult kullanma
        finish();
    }
}