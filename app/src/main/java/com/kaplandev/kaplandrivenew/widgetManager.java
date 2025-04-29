package com.kaplandev.kaplandrivenew;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class widgetManager extends AppWidgetProvider {

  /*  private static final String ACTION_CLICK = "com.kaplandev.kaplandrivenew.ACTION_CLICK";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (ACTION_CLICK.equals(intent.getAction())) {
            RemoteViews loadingViews = new RemoteViews(context.getPackageName(), R.layout.widgetserverstatus);
            loadingViews.setTextViewText(R.id.widget_text, "Bağlantı deneniyor...");
            AppWidgetManager.getInstance(context).updateAppWidget(
                    new ComponentName(context, widgetManager.class), loadingViews
            );

            new Thread(() -> {
                boolean success = false;
                String urlStr = superman.get(context);

                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(3000);
                    conn.setReadTimeout(3000);
                    conn.setRequestMethod("GET");
                    success = conn.getResponseCode() == 200;
                    conn.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                boolean finalSuccess = success;
                new Handler(Looper.getMainLooper()).post(() -> {
                    RemoteViews resultViews = new RemoteViews(context.getPackageName(), R.layout.widgetserverstatus);
                    resultViews.setTextViewText(R.id.widget_text,
                            finalSuccess ? "Sunucu açık ✅" : "Sunucu kapalı ❌");

                    AppWidgetManager.getInstance(context).updateAppWidget(
                            new ComponentName(context, widgetManager.class), resultViews
                    );

                    // 5 saniye sonra geri döndür
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        RemoteViews resetViews = new RemoteViews(context.getPackageName(), R.layout.widgetserverstatus);
                        resetViews.setTextViewText(R.id.widget_text, "Bağlantı kontrolü için tıkla");

                        AppWidgetManager.getInstance(context).updateAppWidget(
                                new ComponentName(context, widgetManager.class), resetViews
                        );
                    }, 5000);
                });

            }).start();
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widgetserverstatus);

            Intent intent = new Intent(context, widgetManager.class);
            intent.setAction(ACTION_CLICK);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            views.setOnClickPendingIntent(R.id.widget_text, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }*/

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widgetserverstatus);
            views.setTextViewText(R.id.widget_text, "Güncellendi!");
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
