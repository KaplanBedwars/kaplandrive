package com.kaplandev.kaplandrivenew;


//FIXME: SETTİNGS EKRANI GELECEK HE
/***import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private SharedPreferences prefs;
    private View rootView;
    private ImageView backgroundImageView;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Tema ayarlarını uygula
        prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        applyTheme(prefs.getInt("THEME", 0));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // UI öğelerini bağla
        rootView = findViewById(R.id.settings_root);
        backgroundImageView = findViewById(R.id.backgroundImageView);
        Button btnChangeUrl = findViewById(R.id.btnChangeUrl);
        Button btnChangeBackground = findViewById(R.id.btnChangeBackground);
        Spinner themeSpinner = findViewById(R.id.themeSpinner);

        // Ayarları başlat
        setupUrlChange(btnChangeUrl);
        setupThemeChange(themeSpinner);
        setupBackgroundImageChange(btnChangeBackground);
    }


    private void setupUrlChange(Button button) {
        button.setOnClickListener(v -> showUrlChangeDialog());
    }

    private void showUrlChangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Yeni Server URL Girin");

        final EditText input = new EditText(this);
        input.setText(prefs.getString("BASE_URL", MainActivity.BASE_URL));
        builder.setView(input);

        builder.setPositiveButton("Kaydet", (dialog, which) -> {
            String newUrl = input.getText().toString().trim();
            if (!newUrl.startsWith("http://") && !newUrl.startsWith("https://")) {
                newUrl = "http://" + newUrl;
            }

            // Kalıcı olarak kaydet
            prefs.edit().putString("BASE_URL", newUrl).apply();

            // MainActivity.BASE_URL değişkenini güncelle
            MainActivity.BASE_URL = newUrl;
        });

        builder.setNegativeButton("İptal", (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    private void setupThemeChange(Spinner spinner) {
        String[] themes = {"Açık Tema", "Koyu Tema"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, themes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        int savedTheme = prefs.getInt("THEME", 0);
        spinner.setSelection(savedTheme);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prefs.edit().putInt("THEME", position).apply();
                recreate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void applyTheme(int theme) {
        if (theme == 0) {

        } else {

        }
    }


    private void setupBackgroundImageChange(Button button) {
        button.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            backgroundImageView.setImageURI(selectedImageUri);
            prefs.edit().putString("BACKGROUND_IMAGE", selectedImageUri.toString()).apply(); // Arka plan kaydet
        }
    }
}***/
