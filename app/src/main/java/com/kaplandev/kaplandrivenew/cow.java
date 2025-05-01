package com.kaplandev.kaplandrivenew;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

/**
 * ----------------------------------------------------------------------------------------------------
 * KaplanBedwars tarafından  https://github.com/Garakrral/Cow/tree/main adresinden alınmıştır.
 *
 * KaplanBedwars tarafından geliştirilmeye devam edilecektir
 *                                                                 A KaplanBedwars orginal 2025
 *-----------------------------------------------------------------------------------------------------
 */
public class cow {

    private static View toastView;
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static boolean isSupermanControlEnabled = true;  // opsiyonel hale getirildi
    private static @LayoutRes int defaultLayout = R.layout.custom_toast_layout; // Değiştirilebilir varsayılan layout

    /**
     * Tek satırdan özel toast göstermek için kısayol
     */
    public static void show(Context context, String message) {
        show(context, message, 3000, defaultLayout);
    }

    /**
     * Tam kontrol ile gösterim
     */
    public static void show(Context context, String message, int durationMillis, @LayoutRes int layoutResId) {
        if (context == null || message == null) return;

        Context appContext = context.getApplicationContext();

        // Overlay izni kontrolü
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(appContext)) {
            requestOverlayPermission(appContext);
            return;
        }

        // Opsiyonel ayar kontrolü
        if (isSupermanControlEnabled && !superman.isbmEnabled(appContext)) {
            return;
        }

        try {
            // Mevcut view varsa kaldır
            if (toastView != null) {
                removeView(appContext);
            }

            LayoutInflater inflater = LayoutInflater.from(appContext);
            View view = inflater.inflate(layoutResId, null);

            TextView textView = view.findViewById(R.id.toast_text);
            if (textView != null) {
                textView.setText(message);
            }

            WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
            if (windowManager == null) return;

            int layoutType = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ?
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                    WindowManager.LayoutParams.TYPE_PHONE;

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

            windowManager.addView(view, params);
            toastView = view;

            // Otomatik kaldırma
            handler.postDelayed(() -> removeView(appContext), durationMillis);

        } catch (Exception e) {
            e.printStackTrace();
            toastView = null;
        }
    }

    /**
     * Görüntüyü güvenli şekilde kaldır
     */
    private static void removeView(Context context) {
        try {
            if (toastView != null) {
                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                if (wm != null) {
                    wm.removeView(toastView);
                }
                toastView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Overlay izni istenirse kullanıcı yönlendirilir
     */
    private static void requestOverlayPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * Layout'u dışarıdan ayarlamak için
     */
    public static void setDefaultLayout(@LayoutRes int layoutResId) {
        defaultLayout = layoutResId;
    }

    /**
     * Ayar kontrolü aç/kapa
     */
    public static void setSupermanControlEnabled(boolean enabled) {
        isSupermanControlEnabled = enabled;
    }
}