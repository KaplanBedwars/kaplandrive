package com.kaplandev.kaplandrivenew;
  //Burası artık sessiz ve sakin. Evet beceremedik (: Daha doğrusu üşendik ve bu class terk edildi. silmeyede üşeniyom. :D
/*import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class githubR {

    // GitHub kullanıcı adı ve repo adı
    private static final String owner = "KaplanBedwars";
    private static final String repo = "kaplandrive";

    // Ana fonksiyon: Çağırması kolay
    public static void getR() {
        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/releases/latest";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/vnd.github.v3+json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            final Handler mainHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> Log.e("githubR", "Bağlantı hatası: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mainHandler.post(() -> Log.e("githubR", "Yanıt başarısız: " + response.code()));
                    return;
                }

                String responseBody = response.body().string();
                try {
                    JSONObject json = new JSONObject(responseBody);
                    String version = json.getString("tag_name");
                    String notes = json.getString("body");
                    String date = json.getString("published_at");

                    mainHandler.post(() -> {
                        Log.d("githubR", "Yeni Sürüm: " + version);
                        Log.d("githubR", "Yayın Tarihi: " + date);
                        Log.d("githubR", "Güncelleme Notları:\n" + notes);
                    });

                } catch (Exception e) {
                    mainHandler.post(() -> Log.e("githubR", "JSON Hatası: " + e.getMessage()));
                }
            }
        });
    }
}
*/