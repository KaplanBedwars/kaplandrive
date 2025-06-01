package com.kaplandev.kaplandrivenew.crashHandlers;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CrashDisplayActivity extends AppCompatActivity {

    private TextView logTextView;
    private Button toggleButton;
    private boolean isExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String crashLog = getIntent().getStringExtra("crash_log");
        if (crashLog == null) crashLog = "Hata detayı alınamadı.";

        // Ana layout
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(64, 64, 64, 64);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        // Açıklama başlığı
        TextView title = new TextView(this);
        title.setText("Uygulama Çöktü");
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 32);
        layout.addView(title);

        // Açıklama metni
        TextView description = new TextView(this);
        description.setText("Uygulama beklenmeyen bir hata nedeniyle kapandı ve hata sebebi indirilenler klasörünüze kaydedildi.\n uygulamayı kapatabilirsiniz.");
        description.setTextSize(16);
        description.setGravity(Gravity.CENTER);
        description.setPadding(0, 0, 0, 32);
        layout.addView(description);

        // Detayları göster/gizle butonu
        toggleButton = new Button(this);
        toggleButton.setText("Detayları Göster");
        layout.addView(toggleButton);

        // Crash log görünümü
        logTextView = new TextView(this);
        logTextView.setText(crashLog);
        logTextView.setVisibility(View.GONE);
        logTextView.setTextIsSelectable(true);

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(logTextView);
        scrollView.setVisibility(View.GONE);
        layout.addView(scrollView);

        toggleButton.setOnClickListener(v -> {
            isExpanded = !isExpanded;
            logTextView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            scrollView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            toggleButton.setText(isExpanded ? "Detayları Gizle" : "Detayları Göster");
        });

        // Uygulamayı kapat butonu


        setContentView(layout);
    }
}
