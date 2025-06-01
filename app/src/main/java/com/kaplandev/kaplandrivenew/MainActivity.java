package com.kaplandev.kaplandrivenew;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kaplandev.kaplandrivenew.crashHandlers.CrashHandler;
import com.kaplandev.kaplandrivenew.tipsSheep.tips;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;



public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabRefresh;
    private ProgressBar progressBar;
    private FileAdapter fileAdapter;
    private FileApi fileApi;
    private ArrayList<Uri> uploadQueue = new ArrayList<>();

    private AlertDialog loadingDialog;

    private ImageButton btnCheckUpdate;
    private static String UPDATE_URL;
    private static String APK_DOWNLOAD_URL;
    private static String CURRENT_VERSION;
    private static String CURTESTV;
    private String pendingDownloadPath;

    public static void init(Context context) {
        UPDATE_URL = context.getString(R.string.update_url);
        APK_DOWNLOAD_URL = context.getString(R.string.apk_download_url);
        CURRENT_VERSION = context.getString(R.string.current_version);
        CURTESTV = context.getString(R.string.current_test_version);
    }

    private static final int REQUEST_OVERLAY_PERMISSION = 1000;
    //base url

   // private static String BASE_URL = "http://192.168.1.38:8080";

   /* private long lastClickTime = 0; // Son tıklama zamanını tutar
    private static final long DOUBLE_CLICK_TIME_DELTA = 1000; //todo: varsayılan 300ms ama ben 1000 yapıcam daha iyi olur

    */
    //FIXME:
    //bugfix zıngırtaları
    // Sınıf seviyesinde ekleyin
    private long backPressedTime = 0;
    private static final int DOUBLE_BACK_PRESS_INTERVAL = 2000; // 2 saniye
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         //bu tospikler artık ayarlar butonu
        btnCheckUpdate = findViewById(R.id.btn_check_update);
        btnCheckUpdate.setOnClickListener(v -> showUrlChangeDialog());
        // ------ --------------------------------------------------

        NotificationUtils.createNotificationChannel(this);
        PermissionHelper.checkNotificationPermission(this);//çalışmazsan yalarım babanneni
        // ActionBar'ı gizle
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        hideCameraCutout();
        checkOverlayPermission(this); // örneğin onCreate() içinde çağır // örneğin onCreate() içinde çağır



        ErrorNotificationUtils.initialize(this);
        // Uygulama başlangıcında (Application sınıfında veya ilk Activity'de):
        SettingsActivity.init(getApplicationContext());
        init(getApplicationContext());








        // UI Elements
        recyclerView = findViewById(R.id.recycler_files);
        progressBar = findViewById(R.id.progress_bar);
        FloatingActionButton fabUpload = findViewById(R.id.fab_upload);
        fabRefresh = findViewById(R.id.fab_refresh);

        // RecyclerView Setup
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fileAdapter = new FileAdapter(new ArrayList<>());
        recyclerView.setAdapter(fileAdapter);

       //uygulama açıldığında güncellemeleri kontrol et:
        checkForUpdate();


        if (superman.isFirstRun(this)) {

            superman.setIfirstrun(this, false);
            superman.setnoEnabled(this, false);
            startActivity(new Intent(this, firstRunActiivty.class));
        }




        fileApi = retrofitInstance().create(FileApi.class);


        // Dosyaları yükle

        hideSystemUI();


        // Yükleme Butonu Tıklama Olayı
        fabUpload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, 69);
        });

        // Yenile Butonu Tıklama Olayı
        fabRefresh.setOnClickListener(v -> {
            fileAdapter.clearFiles(); // Listeyi temizle
            loadFiles(); // Listeyi yeniden yükle
        });
    }
    //retrofit


    //gizli özellik

    private Retrofit retrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(superman.get(this)) // Superman.get() ile URL'yi al
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
    }

    private void hideCameraCutout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(params);
        }

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        // URL değişmiş mi diye kontrol et, Retrofit'i yenile
        fileApi = retrofitInstance().create(FileApi.class);
        tips.show(findViewById(android.R.id.content),
                "Dosyalar", "Dosyaları yüklemek için tıklayın.",
                () -> {
                    loadFiles();
                }, 6000);

    }



    private void showUrlChangeDialog() {

        startActivity(new Intent(this, SettingsActivity.class));
    }


    private FileApi createFileApi() {
        return new Retrofit.Builder()
                .baseUrl(superman.get(this)) // Güncel URL'yi al
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(FileApi.class); // Her çağrıda YENİ bir FileApi oluştur
    }


    private void showLoadingPopup() {
        // Eğer aktivite yoksa veya önceki dialog hala açıksa kapat
        hideLoadingPopup();

        // YENİ DİALOG OLUŞTUR
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.progress_dialog, null);
        builder.setView(view);
        builder.setCancelable(false);
        loadingDialog = builder.create();

        // Arkaplanı şeffaf yap
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Aktivite hala açık mı kontrol et
        if (!isFinishing() && !isDestroyed()) {
            loadingDialog.show();
        }
    }







    private void hideLoadingPopup() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null; // REFERANSI SIFIRLA
        }
    }

    private void checkForUpdate() {
        new Thread(() -> {
            try {
                URL url = new URL(UPDATE_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
                connection.setRequestProperty("User-Agent", "KaplanDrive-App");
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String latestVersion = jsonResponse.getString("tag_name").replace("kaplandrive", "");

                    String currentVersion = CURRENT_VERSION;

                    if (!superman.isUpdatesEnabled(this)) {
                        // Güncellemeler kapalıysa hiçbir şey yapma
                        return;
                    }

                    if (!currentVersion.equals(latestVersion)) {
                        runOnUiThread(() -> startActivity(new Intent(this, update.class)));
                    } else {
                        runOnUiThread(() -> tips.show(findViewById(android.R.id.content), "Bilgi!", "Sürümünüz güncel! YEHUUUUU", null, 800));
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> tips.show(findViewById(android.R.id.content), "İpucu!", "İnternet bağlantınızı kontrol edin!"));
            }
        }).start();
    }





    private void hideSystemUI() {
        Window window = getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // API 30 ve üzeri (Android 11+)
            WindowInsetsController insetsController = window.getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.systemBars());
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            // API 24-29 (Android 7.0 - 10)
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

    private void loadFiles() {
        showLoadingPopup();

        // DİKKAT: createFileApi() direkt FileApi döndürür, .fileApi yazmayın!
        createFileApi().getFiles().enqueue(new Callback<FilesResponse>() {
            @Override
            public void onResponse(Call<FilesResponse> call, Response<FilesResponse> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        fileAdapter.updateFiles(response.body().files);
                    }
                } finally {
                    hideLoadingPopup();
                }
            }

            @Override
            public void onFailure(Call<FilesResponse> call, Throwable t) {
                ErrorNotificationUtils.showErrorNotification("Hata!", "Dosyalar yüklenirken bir hata oluştu..");
                tips.show(
                        findViewById(android.R.id.content),
                        "İpucu!",
                        "Sunucu adresini değiştirmek için tıklayın!",
                        () -> {
                            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        }
                );

                hideLoadingPopup();


                    if (superman.isnoEnabled(MainActivity.this)) {
                        // isNoEnabled true ise bu komut çalışır
                        startActivity(new Intent(MainActivity.this, nointernet.class));
                    } else {
                        // isNoEnabled false ise hiçbir şey yapılmaz
                    }


                // Hata detayını logla (DEBUG için)
                Log.e("API_HATA", "URL: " + superman.get(MainActivity.this) + ", Hata: " + t.getMessage());
            }
        });
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 69 && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    uploadQueue.add(data.getClipData().getItemAt(i).getUri());
                }
            } else if (data.getData() != null) {
                uploadQueue.add(data.getData());
            }
            processUploadQueue();
        }
    }

    private void processUploadQueue() {
        while (!uploadQueue.isEmpty()) {
            Uri uri = uploadQueue.remove(0);
            uploadFile(uri);
        }
    }


    @Override
    protected void onDestroy() {
        hideLoadingPopup(); // Aktivite yok edilirken dialog'u kapat
        super.onDestroy();
    }

    private void uploadFile(Uri uri) {
        showLoadingPopup();

        new Thread(() -> {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                if (inputStream == null) throw new IOException("Dosya açılamadı!");

                // Dosyayı parça parça okuyarak yükleme için bir RequestBody oluştur
                RequestBody requestFile = new RequestBody() {
                    @Override
                    public MediaType contentType() {
                        return MediaType.parse(getContentResolver().getType(uri));
                    }

                    @Override
                    public void writeTo(BufferedSink sink) throws IOException {
                        byte[] buffer = new byte[8192]; // 8KB buffer kullan
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            sink.write(buffer, 0, bytesRead);
                        }
                        inputStream.close(); // InputStream'i kapat
                    }
                };

                MultipartBody.Part part = MultipartBody.Part.createFormData(
                        "file",
                        getFileName(uri),
                        requestFile
                );

                // API çağrısını gerçekleştir
                createFileApi().uploadFile(part).enqueue(new Callback<UploadResponse>() {
                    @Override
                    public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                        runOnUiThread(() -> {
                            hideLoadingPopup();
                            if (response.isSuccessful()) {
                                loadFiles();
                            }
                            processUploadQueue();
                        });
                    }

                    @Override
                    public void onFailure(Call<UploadResponse> call, Throwable t) {
                        runOnUiThread(() -> {
                            ErrorNotificationUtils.showErrorNotification("Hata!", "Yükleme başarısız!");
                            tips.show(findViewById(android.R.id.content), "Hata!", "Yükleme başarısız!");
                            hideLoadingPopup();
                            processUploadQueue();
                        });
                    }
                });
            } catch (Exception e) {
                runOnUiThread(this::hideLoadingPopup);
                processUploadQueue();
            }
        }).start();
    }


    private String getFileName(Uri uri) {
        if (uri == null) return "bilinmeyen_" + System.currentTimeMillis();

        String result = null;
        try {
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    Log.w("FileNameError", "Cursor ile dosya adı alınamadı, alternatif yöntem denenecek", e);
                }
            }
            if (result == null) {
                String path = uri.getPath();
                if (path != null) {
                    try {
                        path = URLDecoder.decode(path, "UTF-8");
                        int cut = path.lastIndexOf('/');
                        result = cut != -1 ? path.substring(cut + 1) : path;
                    } catch (UnsupportedEncodingException e) {
                        Log.e("FileNameError", "URI decode hatası", e);
                        result = path.substring(path.lastIndexOf('/') + 1);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("FileNameError", "Dosya adı alınamadı", e);
        }
        return result != null ? result : UUID.randomUUID().toString();
    }


    private void renameFile(String oldPath, String newName) {
        Map<String, String> body = new HashMap<>();
        body.put("oldPath", oldPath);
        body.put("newName", newName);

        createFileApi().renameFile(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> { // Arka thread'de işle
                        try {
                            String newPath = response.body().string();
                            runOnUiThread(() -> {
                                fileAdapter.updateFilePath(oldPath, newPath);
                                cow.show(MainActivity.this, "Başarılı");
                            });
                        } catch (IOException e) {
                            Log.e("RenameError", "Yanıt okunamadı", e);
                        }
                    }).start();
                } else {
                    cow.show(MainActivity.this, "Hata: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                cow.show(MainActivity.this, "Hata: " + t.getMessage());
            }
        });
    }

    private void downloadFile(String filePath) {
        try {
            // 1. URL construction (safe concatenation)
            String baseUrl = superman.get(this).endsWith("/") ? superman.get(this) : superman.get(this) + "/";
            String sanitizedPath = sanitizeFilePath(filePath);
            String url = Uri.parse(baseUrl).buildUpon().appendEncodedPath(sanitizedPath).build().toString();

            Log.d("DownloadFile", "Download URL: " + url);

            // 2. Get safe filename
            String fileName = getSafeFileName(filePath);

            // 3. Handle different Android versions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                downloadWithMediaStore(fileName, url);
            } else {
                if (checkStoragePermission()) {
                    downloadLegacy(fileName, url);
                } else {
                    // Request storage permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            STORAGE_PERMISSION_CODE);
                }
            }
        } catch (Exception e) {
            Log.e("DownloadFile", "Error: ", e);
            runOnUiThread(() ->
                    tips.show(findViewById(android.R.id.content), "Error", "File download failed: " + e.getMessage()));
        }
    }

    // Define permission request code
    private static final int STORAGE_PERMISSION_CODE = 101;

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, retry the download
                if (pendingDownloadPath != null) {
                    downloadFile(pendingDownloadPath);
                }
            } else {
                tips.show(findViewById(android.R.id.content), "Error", "Storage permission denied");
            }
        }
    }

// Yardımcı metodlar:

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void downloadWithMediaStore(String fileName, String url) {
        try {
            // Dosya adını ve URL'yi encode et
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8");
            String encodedUrl = url.replace(fileName, encodedFileName); // Dosya adını encoded haliyle değiştir

            // Güvenli URL oluştur
            URL urlObj = new URL(encodedUrl);
            String fixedUrl = urlObj.toURI().normalize().toString();

            Log.d("DownloadFile", "Düzeltilmiş Download URL: " + fixedUrl);

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fixedUrl))
                    .setTitle(fileName)
                    .setDescription("Dosyanız indiriliyor...")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setMimeType("application/octet-stream")
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            long downloadId = manager.enqueue(request);

            // İndirme işlemi başlatıldı mı kontrol et
            checkDownloadStatus(downloadId);

        } catch (Exception e) {
            Log.e("DownloadFile", "İndirme hatası:", e);
            runOnUiThread(() ->
                    tips.show(findViewById(android.R.id.content), "Hata", "İndirme başarısız: " + e.getMessage()));
        }
    }


    public void checkOverlayPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                // Kullanıcıya açıklama ve yönlendirme göster
                new AlertDialog.Builder(context)
                        .setTitle("İzin Gerekli")
                        .setMessage("Uygulamanın çalışabilmesi için diğer uygulamaların üstüne çizim izni vermeniz gerekiyor. Uygulama içi ayarlardan bu özelliği kapata bilirsiniz (:")
                        .setPositiveButton("İzni Ver", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + context.getPackageName()));
                            ((Activity) context).startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
                        })
                        .setNegativeButton("Reddet", (dialog, which) -> {
                            cow.show(context, "İzin verilmedi. Uygulama içinde bilgi gösterilemez.");
                            superman.setbmEnabled(this, false);
                        })
                        .setCancelable(false)
                        .show();
            }
        }
    }


    private void checkDownloadStatus(long downloadId) {
        new Thread(() -> {
            DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            boolean downloading = true;

            while (downloading) {
                try {
                    Thread.sleep(2000); // CPU tüketimini azaltmak için bekleme süresi eklendi
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadId);
                    Cursor cursor = manager.query(query);
                    if (cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        int status = cursor.getInt(columnIndex);
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            downloading = false;
                            runOnUiThread(() ->
                                    tips.show(findViewById(android.R.id.content), "Başarılı", "Dosya indirildi!"));
                        } else if (status == DownloadManager.STATUS_FAILED) {
                            downloading = false;
                            runOnUiThread(() ->
                                    tips.show(findViewById(android.R.id.content), "Hata", "İndirme başarısız!"));
                        }
                    }
                    cursor.close();
                } catch (InterruptedException e) {
                    Log.e("DownloadCheck", "İndirme durumu kontrolü kesildi", e);
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }




    private void downloadLegacy(String fileName, String url) {
        // Eski versiyonlar için DownloadManager kullanımı
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .setTitle(fileName)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        long downloadId = manager.enqueue(request);
        handleDownloadResult(downloadId);
    }

    private String sanitizeFilePath(String path) {
        // Path traversal saldırılarını önle
        return path.replaceAll("(\\/\\.\\.\\/)|(\\/\\.\\.$)", "");
    }

    private String getSafeFileName(String path) {
        // Geçersiz karakterleri temizle
        String name = path.substring(path.lastIndexOf("/") + 1);
        return name.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void handleDownloadResult(long downloadId) {
        runOnUiThread(() -> {
            if (downloadId != -1) {
                tips.show(findViewById(android.R.id.content), "Bilgi", "İndirme başladı!");
            } else {
                tips.show(findViewById(android.R.id.content), "Hata", "İndirme başlatılamadı!");
            }
        });
    }

    //TODO: Navigasyon çubuğu kodları burada olacak


    // RecyclerView Adapter
    private class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {
        private List<String> files;

        public FileAdapter(List<String> files) {
            this.files = files;
        }

        public void clearFiles() {
            this.files = new ArrayList<>();
            notifyDataSetChanged();
        }

        public void updateFiles(List<String> newFiles) {
            this.files = newFiles;
            notifyDataSetChanged();
        }

        public void updateFilePath(String oldPath, String newPath) {
            int position = files.indexOf(oldPath);
            if (position != -1) {
                files.set(position, newPath);
                notifyItemChanged(position);
            }
        }

        @NonNull
        @Override
        public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_file, parent, false);
            return new FileViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
            String filePath = files.get(position);
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            holder.fileName.setText(fileName);

            // İkonlar
            if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".png")) {
                holder.fileIcon.setImageResource(R.drawable.ic_image);
            } else if (fileName.toLowerCase().endsWith(".mp4")) {
                holder.fileIcon.setImageResource(R.drawable.ic_video);
            } else {
                holder.fileIcon.setImageResource(R.drawable.ic_file);
            }

            // İndirme Butonu
            holder.btnDownload.setOnClickListener(v -> downloadFile(filePath));

            // Yeniden Adlandırma Butonu
            holder.btnRename.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Yeni İsim");

                final EditText input = new EditText(v.getContext());
                input.setText(fileName);

                builder.setView(input);
                builder.setPositiveButton("Kaydet", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        renameFile(filePath, newName);
                    } else {

                        tips.show(findViewById(android.R.id.content), "Hata!", "Geçersiz isim!");
                    }
                });

                builder.setNegativeButton("İptal", null);
                builder.show();
            });
        }

        @Override
        public int getItemCount() {
            return files.size();
        }

        class FileViewHolder extends RecyclerView.ViewHolder {
            ImageView fileIcon;
            TextView fileName;
            ImageButton btnDownload, btnRename;

            public FileViewHolder(@NonNull View itemView) {
                super(itemView);
                fileIcon = itemView.findViewById(R.id.img_file_type);
                fileName = itemView.findViewById(R.id.txt_file_name);
                btnDownload = itemView.findViewById(R.id.btn_download);
                btnRename = itemView.findViewById(R.id.btn_rename);
            }
        }
    }

    // Retrofit Interface
    public interface FileApi {
        @retrofit2.http.Multipart
        @POST("upload")
        Call<UploadResponse> uploadFile(@retrofit2.http.Part MultipartBody.Part file);

        @GET("files")
        Call<FilesResponse> getFiles();

        @POST("rename")
        Call<ResponseBody> renameFile(@Body Map<String, String> body);
    }

    // Response Classes
    public static class FilesResponse {
        public List<String> files;
    }

    public static class UploadResponse {
        public boolean success;
        public String url;
    }
}