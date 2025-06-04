package com.kaplandev.kaplandrivenew;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;


public class SettingsActivity extends AppCompatActivity {

    private static String UPDATE_URL;
    private static String APK_DOWNLOAD_URL;
    private static String CURRENT_VERSION;
    private static String CURTESTV;
    private boolean userInteracted = false; // Kullanıcının gerçekten tıkladığını ayırmak için




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
    private SwitchMaterial swtchdevSeting;
    private SwitchMaterial httpon;
    private MaterialButton main;
    boolean mismatchDetected = false;// KAPLANGUARDın önemli değişkeni, SİLLME!!!!!!!!!!!!!!!



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        hideActionBar();
        DisplayHelper.hideCameraCutout(this);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        LinearLayout linearLayout = findViewById(R.id.DeveloperSettings); //geliştirici seçenekleri gizleme


        if (!superman.isdevsettingson(this)) {
            linearLayout.setVisibility(View.GONE);
        } else {
            linearLayout.setVisibility(View.VISIBLE);

               messagethedev();

        }


        // View'leri tanımla
        editTextServerUrl = findViewById(R.id.editTextServerUrl);
        switchTips = findViewById(R.id.switchTips);  // 🔴 Yerel değişken TANIMLAMADAN kullan!
        switchErrorNotifications = findViewById(R.id.switchErrorNotifications);
        switchupdate = findViewById(R.id.switchupdate);
        switchsscreen = findViewById(R.id.switchsplashscreen);
        switchno = findViewById(R.id.tospik);
        bm = findViewById(R.id.bm);
        swtchdevSeting = findViewById(R.id.switchDEVSETTİNG);
        TextView statusMessage = findViewById(R.id.textStatusMessage);
        httpon = findViewById(R.id.urlguard);
        main = findViewById(R.id.startMainActivty);
        //--------------------------------------------------------------------------------------------
        //güvenlik kurulumu
        setupSwitchWithConfirmation(swtchdevSeting);
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                boolean devOn = swtchdevSeting.isChecked();   // geliştirici seçeneği
                boolean noOn = switchno.isChecked();          // "no" seçeneği
                boolean updateOn = switchupdate.isChecked();  // güncelleme

                // Beklenen tüm değerler true: Eğer bir tanesi bile değilse uyarı ver
                if (devOn || noOn || !updateOn) {
                    statusMessage.setText("Ayarlarınız kötü durumda.");
                    statusMessage.setTextColor(Color.RED); //kırmızı
                    mismatchDetected = true;
                } else {
                    statusMessage.setText("Ayarlarınız iyi durumda");
                    statusMessage.setTextColor(Color.parseColor("#4CAF50")); // yeşil
                    mismatchDetected = false;
                }

                handler.postDelayed(this, 1000); // her saniye tekrar kontrol
            }
        };

        handler.post(runnable);

        httpon.setOnCheckedChangeListener((buttonView, isChecked) -> {

           superman.sethttpon(this, httpon.isChecked());
        });
        bootloaderInit();


        //-------------------------------------------------------------------


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

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SettingsActivity.this, MainActivity.class));

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

    private void messagethedev(){

        Toast.makeText(getApplicationContext(), "Geliştirici seçenekleri açık, dikkatli kullanın!", Toast.LENGTH_SHORT).show();
    }

    private void setupSwitchWithConfirmation(SwitchMaterial materialSwitch) {
        if (!superman.isdevsettingson(this)) {

            materialSwitch.setOnClickListener(v -> {
                if (materialSwitch.isChecked()) {
                    // 1. İlk engelleme uyarısı
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("KaplanGuard: Uyarı!")
                            .setMessage("Bu ayarı açmanız KaplanGuard tarafından engellendi. Devam etmek istiyor musunuz?")
                            .setPositiveButton("Evet", (dialog1, which1) -> {

                                // 2. Şifre giriş ekranı
                                final EditText input = new EditText(this);
                                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                input.setHint("Güvenlik Kodu");

                                new MaterialAlertDialogBuilder(this)
                                        .setTitle("KaplanGuard: Güvenlik")
                                        .setMessage("Bu ayarı açmak için güvenlik kodunuzu girin.")
                                        .setView(input)
                                        .setPositiveButton("Onayla", (dialog2, which2) -> {
                                            String enteredCode = input.getText().toString();

                                            if (isCodeValid(enteredCode)) {
                                                // 3. Son kırmızı uyarı
                                                TextView warningText = new TextView(this);
                                                warningText.setText("BU AYAR, CİHAZIN KARARLILIĞINI BOZABİLİR!\nDEVAM ETMEK RİSKLİDİR.\n\nRİSKLERİ KABUL EDİYOR MUSUNUZ?");
                                                warningText.setTextColor(Color.RED);
                                                warningText.setPadding(50, 40, 50, 40);
                                                warningText.setTextSize(16);

                                                new MaterialAlertDialogBuilder(this)
                                                        .setTitle("Son Uyarı!")
                                                        .setView(warningText)
                                                        .setPositiveButton("Riskleri kabul ediyorum", (dialog3, which3) -> {
                                                            messagethedev(); // Şifre doğru, onay verildi: işlemi yap
                                                        })
                                                        .setNegativeButton("İptal", (dialog3, which3) -> {
                                                            materialSwitch.setChecked(false); // Geri kapat
                                                        })
                                                        .setOnCancelListener(dialog3 -> {
                                                            materialSwitch.setChecked(false); // Geri kapat
                                                        })
                                                        .show();

                                            } else {
                                                materialSwitch.setChecked(false); // Yanlış şifre → geri kapat
                                                Toast.makeText(this, "Geçersiz güvenlik kodu!", Toast.LENGTH_SHORT).show();
                                            }

                                        })
                                        .setNegativeButton("İptal", (dialog2, which2) -> {
                                            materialSwitch.setChecked(false);
                                        })
                                        .setOnCancelListener(dialog2 -> {
                                            materialSwitch.setChecked(false);
                                        })
                                        .show();

                            })
                            .setNegativeButton("Hayır", (dialog1, which1) -> {
                                materialSwitch.setChecked(false);
                            })
                            .setOnCancelListener(dialog1 -> {
                                materialSwitch.setChecked(false);
                            })
                            .show();
                }
            });

        } else {
            // Geliştirici ayarları zaten açıksa doğrudan açılabilir.
        }
    }


    private boolean isCodeValid(String code) {
        return hash(code).equals("4b3dd3582cab4e3144a67c03e7bf9b09");
    }

    // MD5 hash fonksiyonu
    private String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            BigInteger number = new BigInteger(1, messageDigest);
            StringBuilder hashText = new StringBuilder(number.toString(16));
            while (hashText.length() < 32) {
                hashText.insert(0, "0");
            }
            return hashText.toString();
        } catch (Exception e) {
            return "";
        }
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
        swtchdevSeting.setChecked(superman.isdevsettingson(this));
        httpon.setChecked(superman.ishttpon(this));

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
        new MaterialAlertDialogBuilder(context)
                .setTitle("Test: Uygulamayı Çökert")
                .setMessage("Uygulamayı çökertmek isityormusunuz? BU İŞLEM GERİ ALINAMAZ")
                .setPositiveButton("Evet", (dialog, which) -> {
                    throw new RuntimeException("TestCrash");
                })
                .setNegativeButton("Hayır", null)
                .show();
    }

    private void saveSettings() {
        String url = editTextServerUrl.getText().toString().trim();
        if (mismatchDetected) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("KaplanGuard: Uyarı!")
                    .setMessage("Devam etmeniz KaplanGuard tarafından engellendi. Devam etmek isityormusunuz?")
                    .setPositiveButton("Evet, devam et", (dialog, which) -> {
                        if (url.contains("http://") && superman.ishttpon(SettingsActivity.this)) {
                            new MaterialAlertDialogBuilder(SettingsActivity.this)
                                    .setTitle("KaplanGuard: Uyarı")
                                    .setMessage("HTTP içeren bir adres girilmiş ve bu kullanım devre dışı bırakılmış.\nLütfen HTTPS kullanın yada gerekli ayarı kapatın.")
                                    .setPositiveButton("Tamam", null)
                                    .show();
                            return; // kaydetme işlemini durdur
                        }else{

                            proceedToSave();
                        }
                    })
                    .setNegativeButton("İptal", (dialog, which) -> {
                        dialog.dismiss(); // bir şey yapma
                    })
                    .show();
        } else {
            if (url.contains("http://") && superman.ishttpon(SettingsActivity.this)) {
                new MaterialAlertDialogBuilder(SettingsActivity.this)
                        .setTitle("KaplanGuard: Uyarı")
                        .setMessage("HTTP içeren bir adres girilmiş ve bu kullanım devre dışı bırakılmış.\nLütfen HTTPS kullanın yada gerekli ayarı kapatın.")
                        .setPositiveButton("Tamam", null)
                        .show();
                return; // kaydetme işlemini durdur
            }else{

                proceedToSave();
            }
        }
    }

    private void proceedToSave() {
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

        // 3. Protokol ekleme
        if (!newUrl.startsWith("http://") && !newUrl.startsWith("https://")) {
            newUrl = "http://" + newUrl;
        }

        // Ayarları kaydet
        superman.set(this, newUrl);
        superman.setTipsEnabled(this, switchTips.isChecked());
        superman.setErrorNotificationsEnabled(this, switchErrorNotifications.isChecked());
        superman.setUpdatesEnabled(this, switchupdate.isChecked());
        superman.setSscreenEnabled(this, switchsscreen.isChecked());
        superman.setnoEnabled(this, switchno.isChecked());
        superman.setbmEnabled(this, bm.isChecked());
        superman.setdevsettingson(this, swtchdevSeting.isChecked());
        superman.sethttpon(this, httpon.isChecked());

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

    private void bootloaderInit(){

        String[] classNames = {
                "MainActivity",
                "SettingsActivity",
                "UnsupportedVersionActivity",
                "update",
                "Hiçbirşey"

        };

        String[] classFullPaths = {
                "com.kaplandev.kaplandrivenew.MainActivity",
                "com.kaplandev.kaplandrivenew.SettingsActivity",
                "com.kaplandev.kaplandrivenew.UnsupportedVersionActivity",
                "com.kaplandev.kaplandrivenew.update",
                "com.fgfgfgfggfg"
        };

        AutoCompleteTextView dropdown = findViewById(R.id.bootClassDropdown);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, classNames);
        dropdown.setAdapter(adapter);

// Önceki seçimi göster
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String savedClass = prefs.getString("boot_class", null);
        if (savedClass != null) {
            for (int i = 0; i < classFullPaths.length; i++) {
                if (classFullPaths[i].equals(savedClass)) {
                    dropdown.setText(classNames[i], false);
                    break;
                }
            }
        }

// Seçim yapıldığında kaydet
        dropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedClassPath = classFullPaths[position];
            prefs.edit().putString("boot_class", selectedClassPath).apply();
            Toast.makeText(this, "Seçilen sınıf: " + classNames[position], Toast.LENGTH_SHORT).show();
        });

    }



}