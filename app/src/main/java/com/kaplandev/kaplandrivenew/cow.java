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

import java.util.LinkedList;
import java.util.Queue;

public class cow {

    private static View toastView;
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static boolean isSupermanControlEnabled = true;
    private static @LayoutRes int defaultLayout = R.layout.custom_toast_layout;

    private static final Queue<ToastData> toastQueue = new LinkedList<>();
    private static final int MAX_QUEUE = 5;

    private static class ToastData {
        Context context;
        String message;
        int duration;
        int layoutRes;

        ToastData(Context context, String message, int duration, int layoutRes) {
            this.context = context.getApplicationContext();
            this.message = message;
            this.duration = duration;
            this.layoutRes = layoutRes;
        }
    }

    public static void show(Context context, String message) {
        show(context, message, 3000, defaultLayout);
    }

    public static void show(Context context, String message, int durationMillis, @LayoutRes int layoutResId) {
        if (context == null || message == null) return;

        Context appContext = context.getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(appContext)) {
            requestOverlayPermission(appContext);
            return;
        }

        if (isSupermanControlEnabled && !superman.isbmEnabled(appContext)) {
            return;
        }

        ToastData toastData = new ToastData(appContext, message, durationMillis, layoutResId);

        if (toastView != null) {
            if (toastQueue.size() >= MAX_QUEUE) {
                android.util.Log.w("Cow", "⚠️ Toast kuyruğu dolu, yeni mesaj eklenmedi.");
                return;
            }
            toastQueue.add(toastData);
            updateQueueIndicator(); // gösteriliyorsa güncelle
        } else {
            showToast(toastData);
        }
    }

    private static void showToast(ToastData data) {
        try {
            LayoutInflater inflater = LayoutInflater.from(data.context);
            View view = inflater.inflate(data.layoutRes, null);

            TextView textView = view.findViewById(R.id.toast_text);
            if (textView != null) {
                textView.setText(data.message);
            }

            TextView queueView = view.findViewById(R.id.toast_queue);
            if (queueView != null) {
                int remaining = toastQueue.size();
                if (remaining > 0) {
                    queueView.setVisibility(View.VISIBLE);
                    queueView.setText("1/" + (remaining + 1));
                } else {
                    queueView.setVisibility(View.GONE);
                }
            }

            WindowManager windowManager = (WindowManager) data.context.getSystemService(Context.WINDOW_SERVICE);
            if (windowManager == null) return;

            int layoutType = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    : WindowManager.LayoutParams.TYPE_PHONE;

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

            handler.postDelayed(() -> {
                removeView(data.context);
                if (!toastQueue.isEmpty()) {
                    showToast(toastQueue.poll());
                }
            }, data.duration);

        } catch (Exception e) {
            e.printStackTrace();
            toastView = null;
        }
    }

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

    private static void requestOverlayPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static void setDefaultLayout(@LayoutRes int layoutResId) {
        defaultLayout = layoutResId;
    }

    public static void setSupermanControlEnabled(boolean enabled) {
        isSupermanControlEnabled = enabled;
    }

    private static void updateQueueIndicator() {
        if (toastView == null) return;
        TextView queueText = toastView.findViewById(R.id.toast_queue);
        if (queueText != null) {
            int remaining = toastQueue.size();
            queueText.setVisibility(View.VISIBLE);
            queueText.setText("1/" + (remaining + 1));
        }
    }
}
