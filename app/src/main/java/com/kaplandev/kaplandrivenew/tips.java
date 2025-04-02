package com.kaplandev.kaplandrivenew;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;

public class tips {

    public static void show(View view, String tipTitle, String tipMessage) {
            // Eğer view null ise veya tips kapalıysa iptal et
            if (view == null || !superman.isTipsEnabled(view.getContext())) {
                return;
            }
        // Eğer View null ise işlemi iptal et

        // Snackbar oluştur
        Snackbar snackbar = Snackbar.make(view, "💡 " + tipTitle + "\n" + tipMessage, Snackbar.LENGTH_LONG);

        // Snackbar'ın görünümünü özelleştir
        View snackbarView = snackbar.getView();
        TextView snackbarText = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);

        // Yazı stilini güncelle
        snackbarText.setTextSize(16);
        snackbarText.setMaxLines(3); // Uzun mesajları sınırla
        snackbarText.setGravity(Gravity.CENTER);

        // Tema kontrolü (Karanlık veya Açık mod)
        Context context = view.getContext();
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            snackbarView.setBackgroundColor(Color.DKGRAY); // Karanlık mod için koyu renk
            snackbarText.setTextColor(Color.WHITE);
        } else {
            snackbarView.setBackgroundColor(Color.WHITE); // Açık mod için beyaz arka plan
            snackbarText.setTextColor(Color.BLACK);
        }

        snackbar.show(); // Snackbar'ı göster
    }
}
