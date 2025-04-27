package com.kaplandev.kaplandrivenew;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.text.HtmlCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;


import java.util.regex.Pattern;

public class doc extends AppCompatActivity {

    private TextView contentTitle;
    private TextView contentTextView;
    private MaterialToolbar toolbar;
    private SearchView searchView;

    // Mevcut içerikleri saklamak için
    private String baslangicContent;
    private String ozelliklerContent;
    private String sssContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.documentation_page);

        // Toolbar'ı ayarla
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
         hideCameraCutout();
        // View'leri bağla
        contentTitle = findViewById(R.id.contentTitle);
        contentTextView = findViewById(R.id.contentTextView);

        // İçerikleri yükle
        loadBaslangicContent();
        baslangicContent = contentTextView.getText().toString();
        loadOzelliklerContent();
        ozelliklerContent = contentTextView.getText().toString();
        loadSSSContent();
        sssContent = contentTextView.getText().toString();


        // Chip tıklama dinleyicileri
        Chip chipBaslangic = findViewById(R.id.chipBaslangic);
        Chip chipOzellikler = findViewById(R.id.chipOzellikler);
        Chip chipSSS = findViewById(R.id.chipSSS);

        chipBaslangic.setOnClickListener(v -> {
            loadBaslangicContent();
            baslangicContent = contentTextView.getText().toString();
        });
        chipOzellikler.setOnClickListener(v -> {
            loadOzelliklerContent();
            ozelliklerContent = contentTextView.getText().toString();
        });
        chipSSS.setOnClickListener(v -> {
            loadSSSContent();
            sssContent = contentTextView.getText().toString();
        });

        // SearchView ayarları
        searchView = findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false); // Başlangıçta geniş halde
        searchView.setQueryHint("Ara...");
        searchView.clearFocus();
        hideActionBar();

        // SearchView dinleyicileri
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterContent(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterContent(newText);
                return true;
            }
        });

        // SearchView genişletme ayarı
        searchView.setOnSearchClickListener(v -> searchView.setIconified(false));
    }

    private void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
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

    private void filterContent(String query) {
        String currentHtmlContent;

        // Hangi içeriğin gösterildiğini belirle
        if (contentTitle.getText().toString().contains("Başlangıç")) {
            currentHtmlContent = baslangicContent;
        } else if (contentTitle.getText().toString().contains("Özellikler")) {
            currentHtmlContent = ozelliklerContent;
        } else {
            currentHtmlContent = sssContent;
        }

        if (query.isEmpty()) {
            // Arama boşsa orijinal içeriği göster
            contentTextView.setText(HtmlCompat.fromHtml(currentHtmlContent, HtmlCompat.FROM_HTML_MODE_COMPACT));
            return;
        }

        // HTML tag'lerini koruyarak arama yap
        String highlightedContent = currentHtmlContent.replaceAll(
                "(?i)(" + Pattern.quote(query) + ")(?![^<]*>|[^<>]*</)",
                "<span style='background-color:#FFFF00;'>$1</span>"
        );

        // Vurgulanmış içeriği göster
        contentTextView.setText(HtmlCompat.fromHtml(highlightedContent, HtmlCompat.FROM_HTML_MODE_COMPACT));
    }

    private void loadBaslangicContent() {
        contentTitle.setText("Başlangıç Kılavuzu");
        String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 800px; margin: 0 auto; color: #333;'>" +
                "<h2 style='color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 8px;'>Uygulamaya Giriş</h2>" +
                "<div style='background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin-bottom: 20px;'>" +
                "<p style='line-height: 1.6;'>BURASI DAHA AKTİFLEŞMEDİ VE BURADA YAZAN HİÇBİRŞEY GERÇEK DEĞİLDİR!</p>" +
                "</div>" +

                "<h2 style='color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 8px; margin-top: 30px;'>Hesap Oluşturma</h2>" +
                "<div style='background-color: #e8f4fc; padding: 15px; border-radius: 5px; border-left: 4px solid #3498db;'>" +
                "<p style='font-weight: 500; margin-bottom: 10px;'>Yeni bir hesap oluşturmak için aşağıdaki adımları izleyin:</p>" +
                "<ol style='line-height: 1.8; padding-left: 20px;'>" +
                "<li style='margin-bottom: 8px;'><span style='font-weight: 600; color: #2980b9;'>Adım 1:</span> Ana ekrandaki 'Kayıt Ol' butonuna tıklayın</li>" +
                "<li style='margin-bottom: 8px;'><span style='font-weight: 600; color: #2980b9;'>Adım 2:</span> Açılan formda gerekli bilgileri eksiksiz doldurun</li>" +
                "<li style='margin-bottom: 8px;'><span style='font-weight: 600; color: #2980b9;'>Adım 3:</span> E-posta adresinize gelen doğrulama linkine tıklayın</li>" +
                "</ol>" +
                "<p style='font-style: italic; margin-top: 10px; color: #7f8c8d;'>Not: Şifreniz en az 8 karakter ve büyük/küçük harf ile özel karakter içermelidir.</p>" +
                "</div>" +
                "</div>";
        contentTextView.setText(HtmlCompat.fromHtml(htmlContent, HtmlCompat.FROM_HTML_MODE_COMPACT));
    }

    private void loadOzelliklerContent() {
        contentTitle.setText("Özellikler");
        String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 800px; margin: 0 auto; color: #333;'>" +
                "<h2 style='color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 8px;'>Temel Özellikler</h2>" +

                "<div style='display: flex; flex-wrap: wrap; gap: 15px; margin-bottom: 25px;'>" +
                "<div style='flex: 1; min-width: 250px; background: white; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); padding: 15px;'>" +
                "<h3 style='color: #3498db; margin-top: 0;'>Özellik 1</h3>" +
                "<p style='line-height: 1.6;'>Bu özellik sayesinde verilerinizi kolayca yönetebilir, istatistiklerinizi görüntüleyebilir ve özelleştirilmiş raporlar oluşturabilirsiniz.</p>" +
                "</div>" +

                "<div style='flex: 1; min-width: 250px; background: white; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); padding: 15px;'>" +
                "<h3 style='color: #3498db; margin-top: 0;'>Özellik 2</h3>" +
                "<p style='line-height: 1.6;'>Gelişmiş filtreleme seçenekleri ile büyük veri setlerinde hızlı arama yapabilir, sonuçları istediğiniz formatta dışa aktarabilirsiniz.</p>" +
                "</div>" +
                "</div>" +

                "<h2 style='color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 8px; margin-top: 20px;'>Gelişmiş Özellikler</h2>" +
                "<div style='background: linear-gradient(to right, #f5f7fa, #e4edf9); padding: 20px; border-radius: 8px;'>" +
                "<p style='line-height: 1.6; margin-bottom: 15px;'>Uygulamanın premium özelliklerini keşfetmek için ayarlar menüsündeki <span style='background: #3498db; color: white; padding: 2px 6px; border-radius: 4px;'>Gelişmiş</span> sekmesini ziyaret edin.</p>" +
                "<ul style='line-height: 1.8; padding-left: 20px;'>" +
                "<li>Otomatik yedekleme ve senkronizasyon</li>" +
                "<li>Özel tema oluşturma</li>" +
                "<li>API entegrasyonları</li>" +
                "<li>Ekip yönetim araçları</li>" +
                "</ul>" +
                "</div>" +
                "</div>";
        contentTextView.setText(HtmlCompat.fromHtml(htmlContent, HtmlCompat.FROM_HTML_MODE_COMPACT));
    }

    private void loadSSSContent() {
        contentTitle.setText("Sık Sorulan Sorular");
        String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 800px; margin: 0 auto; color: #333;'>" +
                "<h2 style='color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 8px;'>Genel Sorular</h2>" +

                "<div style='margin-bottom: 20px;'>" +
                "<div style='background-color: #f8f9fa; padding: 12px 15px; border-radius: 5px 5px 0 0; font-weight: 600; color: #2c3e50;'>" +
                "❓ Nasıl giriş yapabilirim?" +
                "</div>" +
                "<div style='background-color: white; padding: 15px; border: 1px solid #eee; border-radius: 0 0 5px 5px; border-top: none;'>" +
                "<p style='line-height: 1.6; margin: 0;'>Ana ekrandaki <span style='color: #3498db; font-weight: 500;'>'Giriş Yap'</span> butonuna tıklayarak, kayıtlı e-posta ve şifrenizle hesabınıza erişebilirsiniz. Sosyal medya hesaplarınızla hızlı giriş de yapabilirsiniz.</p>" +
                "</div>" +
                "</div>" +

                "<div style='margin-bottom: 20px;'>" +
                "<div style='background-color: #f8f9fa; padding: 12px 15px; border-radius: 5px 5px 0 0; font-weight: 600; color: #2c3e50;'>" +
                "❓ Şifremi unuttum ne yapmalıyım?" +
                "</div>" +
                "<div style='background-color: white; padding: 15px; border: 1px solid #eee; border-radius: 0 0 5px 5px; border-top: none;'>" +
                "<p style='line-height: 1.6; margin: 0;'>Giriş ekranındaki <span style='color: #3498db; font-weight: 500;'>'Şifremi Unuttum'</span> bağlantısına tıklayarak şifre sıfırlama maili talep edebilirsiniz. Mail 5 dakika içinde gelmezse spam klasörünüzü kontrol edin.</p>" +
                "</div>" +
                "</div>" +

                "<h2 style='color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 8px; margin-top: 30px;'>Teknik Sorunlar</h2>" +

                "<div style='margin-bottom: 20px;'>" +
                "<div style='background-color: #f8f9fa; padding: 12px 15px; border-radius: 5px 5px 0 0; font-weight: 600; color: #2c3e50;'>" +
                "⚠️ Uygulama çöküyor ne yapmalıyım?" +
                "</div>" +
                "<div style='background-color: white; padding: 15px; border: 1px solid #eee; border-radius: 0 0 5px 5px; border-top: none;'>" +
                "<ol style='line-height: 1.8; padding-left: 20px; margin: 0;'>" +
                "<li>Cihazınızı yeniden başlatmayı deneyin</li>" +
                "<li>Uygulamayı en son sürüme güncelleyin</li>" +
                "<li>Önbelleği temizleyin (Ayarlar > Uygulamalar)</li>" +
                "<li>Sorun devam ederse <span style='color: #3498db;'>destek@ornek.com</span> adresine bildirin</li>" +
                "</ol>" +
                "</div>" +
                "</div>" +
                "</div>";
        contentTextView.setText(HtmlCompat.fromHtml(htmlContent, HtmlCompat.FROM_HTML_MODE_COMPACT));
    }
}