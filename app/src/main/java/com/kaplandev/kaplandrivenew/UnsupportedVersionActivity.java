package com.kaplandev.kaplandrivenew;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class UnsupportedVersionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unsupported_version);

        // TextView'ı bul ve metnini güncelle
        TextView warningText = findViewById(R.id.warning_text);
        warningText.setText("CİHAZINIZ DESTEKLENMİYOR!");

        // Detay butonu işlevselliği
        Button detailsButton = findViewById(R.id.details_button);
        TextView detailsText = findViewById(R.id.details_text);

        detailsButton.setOnClickListener(v -> {
            if (detailsText.getVisibility() == View.VISIBLE) {
                detailsText.setVisibility(View.GONE);
            } else {
                detailsText.setVisibility(View.VISIBLE);
            }
        });

        // Çıkış butonu işlevselliği
        Button exitButton = findViewById(R.id.exit_button);
        exitButton.setOnClickListener(v -> {
            finishAffinity(); // Tüm aktiviteleri kapat
            System.exit(0); // Uygulamayı tamamen sonlandır
        });

        // Hata bildirimlerini göster
        showErrorNotifications();
    }

    private void showErrorNotifications() {
        // ErrorNotificationUtils kullanarak bildirim göster
        ErrorNotificationUtils.showErrorNotification(
                "Hata!",
                "Cihazınız desteklenmiyor"
        );

        // ErrorTips kullanarak toast/snackbar göster
        ErrorTips.show(
                findViewById(android.R.id.content),
                "Hata!",
                "Telefonunuz desteklemiyor!"
        );
    }
}