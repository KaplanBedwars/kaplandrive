package com.kaplandev.kaplandrivenew;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class ErrorTips {

    public static void show(View view, String errorTitle, String errorMessage) {
        if (view == null || !superman.isTipsEnabled(view.getContext())) {
            return;
        }

        // Snackbar oluştur
        Snackbar snackbar = Snackbar.make(view, "⚠️ " + errorTitle + "\n" + errorMessage, Snackbar.LENGTH_LONG);

        // Snackbar'ın görünümünü özelleştir
        View snackbarView = snackbar.getView();
        TextView snackbarText = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);

        // Yazı stilini ve vurguyu güncelle
        snackbarText.setTextSize(18);
        snackbarText.setMaxLines(4); // Daha fazla satır gösterebilir
        snackbarText.setGravity(Gravity.CENTER);
        snackbarText.setTypeface(null, android.graphics.Typeface.BOLD); // Kalın font

        // Tema kontrolü (Karanlık veya Açık mod)
        Context context = view.getContext();
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            snackbarView.setBackgroundColor(Color.RED); // Karanlık modda da kırmızı arka plan
            snackbarText.setTextColor(Color.WHITE);
        } else {
            snackbarView.setBackgroundColor(Color.RED); // Açık modda da kırmızı arka plan
            snackbarText.setTextColor(Color.WHITE);
        }

        snackbar.show(); // Snackbar'ı göster
    }
}
