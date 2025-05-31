package com.kaplandev.kaplandrivenew.tipsSheep;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kaplandev.kaplandrivenew.R;
import com.kaplandev.kaplandrivenew.superman;

import java.util.LinkedList;
import java.util.Queue;

public class tips {

    private static final int MAX_TIP_QUEUE = 5;
    private static final Queue<TipData> tipQueue = new LinkedList<>();
    private static View currentTipView = null;
    private static Handler handler = new Handler();

    // Tip veri modeli
    private static class TipData {
        View anchorView;
        String title;
        String message;
        Runnable onAction;
        int durationMs;

        TipData(View anchorView, String title, String message, Runnable onAction, int durationMs) {
            this.anchorView = anchorView;
            this.title = title;
            this.message = message;
            this.onAction = onAction;
            this.durationMs = durationMs;
        }
    }

    // Overload metotlar
    public static void show(View anchorView, String title, String message) {
        show(anchorView, title, message, null, 0);
    }

    public static void show(View anchorView, String title, String message, Runnable onAction) {
        show(anchorView, title, message, onAction, 0);
    }

    public static void show(View anchorView, String title, String message, Runnable onAction, int durationMs) {
        Context context = anchorView.getContext();
        if (!(context instanceof Activity) || !superman.isTipsEnabled(context)) {
            return;
        }

        TipData tip = new TipData(anchorView, title, message, onAction, durationMs);

        if (currentTipView != null) {
            if (tipQueue.size() >= MAX_TIP_QUEUE) {
                Log.w("Tips", "⚠️ Çok fazla tips sırada. Yeni tips eklenmedi.");
                return;
            }
            tipQueue.add(tip);
            updateQueueIndicator();
            return;
        }

        showTip(tip);
    }

    private static void showTip(TipData tip) {
        Context context = tip.anchorView.getContext();
        Activity activity = (Activity) context;
        ViewGroup rootView = activity.findViewById(android.R.id.content);

        LayoutInflater inflater = LayoutInflater.from(context);
        View tipView = inflater.inflate(R.layout.tip_box, rootView, false);

        TextView titleText = tipView.findViewById(R.id.tipTitle);
        TextView messageText = tipView.findViewById(R.id.tipMessage);
        TextView queueCount = tipView.findViewById(R.id.tipQueueCount);
        Button okButton = tipView.findViewById(R.id.tipOkButton);
        Button actionButton = tipView.findViewById(R.id.tipActionButton);

        titleText.setText("💡 " + tip.title);
        messageText.setText(tip.message);

        // Tema
        int nightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
            tipView.setBackgroundResource(R.drawable.tip_box_background_dark);
            titleText.setTextColor(0xFFFFFFFF);
            messageText.setTextColor(0xFFDDDDDD);
            queueCount.setTextColor(0xFFBBBBBB);
        }

        // Sıra bilgisi
        int remaining = tipQueue.size();
        if (remaining > 0) {
            queueCount.setVisibility(View.VISIBLE);
            queueCount.setText("1/" + (remaining + 1));
        } else {
            queueCount.setVisibility(View.GONE);
        }

        currentTipView = tipView;
        rootView.addView(tipView);

        Runnable removeTip = () -> {
            rootView.removeView(tipView);
            currentTipView = null;
            if (!tipQueue.isEmpty()) {
                showTip(tipQueue.poll());
            }
        };

        okButton.setOnClickListener(v -> removeTip.run());

        if (tip.onAction != null) {
            actionButton.setVisibility(View.VISIBLE);
            actionButton.setOnClickListener(v -> {
                removeTip.run();
                tip.onAction.run();
            });
        }

        if (tip.durationMs > 0) {
            handler.postDelayed(removeTip, tip.durationMs);
        }
    }

    private static void updateQueueIndicator() {
        if (currentTipView == null) return;

        TextView queueCount = currentTipView.findViewById(R.id.tipQueueCount);
        if (queueCount != null) {
            int remaining = tipQueue.size();
            queueCount.setVisibility(View.VISIBLE);
            queueCount.setText("1/" + (remaining + 1));
        }
    }
}
