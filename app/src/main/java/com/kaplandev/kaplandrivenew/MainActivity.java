package com.kaplandev.kaplandrivenew;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationUtils.createNotificationChannel(this);

        // ActionBar'ı gizle
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Retrofit Initialization
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.38:8080/") // IP'Yİ GÜNCELLE!
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
        fileApi = retrofit.create(FileApi.class);

        // UI Elements
        recyclerView = findViewById(R.id.recycler_files);
        progressBar = findViewById(R.id.progress_bar);
        FloatingActionButton fabUpload = findViewById(R.id.fab_upload);
        fabRefresh = findViewById(R.id.fab_refresh);

        // RecyclerView Setup
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fileAdapter = new FileAdapter(new ArrayList<>());
        recyclerView.setAdapter(fileAdapter);


        //tudey yurugit



        // Dosyaları yükle
        loadFiles();
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
    //bildirimler:


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
        fileApi.getFiles().enqueue(new Callback<FilesResponse>() {
            @Override
            public void onResponse(Call<FilesResponse> call, Response<FilesResponse> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        fileAdapter.updateFiles(response.body().files);
                    }
                } finally {
                    hideLoadingPopup(); // HER DURUMDA KAPAT
                }
            }

            @Override
            public void onFailure(Call<FilesResponse> call, Throwable t) {
                NotificationUtils.showNotification(MainActivity.this, "Hata!", "Dosyalar yüklenirken bir hata oluştu..");
                hideLoadingPopup();
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
        if (!uploadQueue.isEmpty()) {
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
        showLoadingPopup(); // Yükleme başlarken popup göster
        new Thread(() -> {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                byte[] fileBytes = new byte[inputStream.available()];
                inputStream.read(fileBytes);

                RequestBody requestFile = RequestBody.create(
                        MediaType.parse(getContentResolver().getType(uri)),
                        fileBytes
                );

                MultipartBody.Part part = MultipartBody.Part.createFormData(
                        "file",
                        getFileName(uri),
                        requestFile
                );

                fileApi.uploadFile(part).enqueue(new Callback<UploadResponse>() {
                    @Override
                    public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                        try {
                            if (response.isSuccessful()) {
                                loadFiles(); // Dosyaları yeniden yükle
                            }
                        } finally {
                            runOnUiThread(() -> hideLoadingPopup()); // Popup'ı kapat
                        }
                    }

                    @Override
                    public void onFailure(Call<UploadResponse> call, Throwable t) {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Yükleme başarısız!", Toast.LENGTH_SHORT).show();
                            hideLoadingPopup(); // Popup'ı kapat
                        });
                    }
                });
            } catch (Exception e) {
                Log.e("UploadError", e.getMessage());
                runOnUiThread(() -> hideLoadingPopup()); // Hata durumunda popup'ı kapat
            }
        }).start();
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                Log.e("FileNameError", "Dosya adı alınamadı", e);
            }
        }
        if (result == null) {
            result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                result = cut != -1 ? result.substring(cut + 1) : result;
            }
        }
        return result != null ? result : UUID.randomUUID().toString();
    }

    private void renameFile(String oldPath, String newName) {
        Map<String, String> body = new HashMap<>();
        body.put("oldPath", oldPath);
        body.put("newName", newName);

        fileApi.renameFile(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String newPath = response.body().string();
                        fileAdapter.updateFilePath(oldPath, newPath);
                        NotificationUtils.showNotification(MainActivity.this, "Tamamlandı!", "İşlem başarılı.");
                    } catch (IOException e) {
                        Log.e("RenameError", "Yanıt okunamadı", e);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Hata: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
              //  Toast.makeText(MainActivity.this, "Hata: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                NotificationUtils.showNotification(MainActivity.this, "HATA!", "Bilinmeyen hata oluştu.");
            }
        });
    }

    private void downloadFile(String filePath) {
        String url = "http://192.168.1.38:8080" + filePath; // IP'Yİ GÜNCELLE!
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .setTitle(fileName)
                .setDescription("İndiriliyor...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        manager.enqueue(request);

        NotificationUtils.showNotification(MainActivity.this, "Bilgi", "İndirem başladı.");
    }

    private void showUploadNotification(int remaining) {
        // TODO: Bildirim gösterimi için kodu buraya ekle
    }

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
                        Toast.makeText(v.getContext(), "Geçersiz isim!", Toast.LENGTH_SHORT).show();
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