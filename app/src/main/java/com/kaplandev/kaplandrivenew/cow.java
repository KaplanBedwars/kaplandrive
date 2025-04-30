package com.kaplandev.kaplandrivenew;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class cow {

    private static View toastView;



    public static void show(Context context, String message) {
        show(context, message, 3000); // Varsayılan 3 saniye göster
    }



    public static void show(Context context, String message, int durationMillis) {
        // Eğer zaten gösteriliyorsa tekrar gösterme

        if (!superman.isbmEnabled(context)) {
            return;
        }

        if (toastView != null) {
            return;
        }


        Context appContext = context.getApplicationContext();
        WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);

        LayoutInflater inflater = LayoutInflater.from(appContext);
        toastView = inflater.inflate(R.layout.custom_toast_layout, null);

        TextView text = toastView.findViewById(R.id.toast_text);
        text.setText(message);

        int layoutType;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutType = WindowManager.LayoutParams.TYPE_PHONE;
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.y = 100;

        try {
            windowManager.addView(toastView, params);
        } catch (Exception e) {
            e.printStackTrace();
            toastView = null;
            return;
        }

        new Handler().postDelayed(() -> {
            try {
                if (toastView != null) {
                    windowManager.removeView(toastView);
                    toastView = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, durationMillis);
    }
}
