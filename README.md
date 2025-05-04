
# A KaplanBedwars Orginal team: KaplanDrive

#### Yoltaşı:
- [Gizlilik Politikası](https://github.com/KaplanBedwars/kaplandrive/blob/main/TERMS.md)

- [English](https://github.com/KaplanBedwars/kaplandrive/blob/main/README_english.md)

### Rozetler:

[![Orginal](https://github.com/KaplanBedwars/KaplanBedwars/blob/main/q(1).png)](https://choosealicense.com/licenses/mit/)
[![GPLv3 License](https://img.shields.io/badge/License-GPL%20v3-yellow.svg)](https://opensource.org/licenses/)
[![GPLv3 License](https://img.shields.io/badge/Language-Java-blue)](https://opensource.org/licenses/)
[![GPLv3 License](https://img.shields.io/badge/Platform-Android-Green
)](https://opensource.org/licenses/)
![A KaplanBedwars Original](https://img.shields.io/badge/A_KaplanBedwars_Original-%E2%AD%90-orange)
![Status](https://img.shields.io/badge/status-stable-brightgreen)
![Mod](https://img.shields.io/badge/type-Android_App-red)
![Made with ❤️](https://img.shields.io/badge/Made_with-KaplanBedwars%E2%9D%A4-red)
![Not a Fork](https://img.shields.io/badge/100%25-Original-orange)
![No Ads](https://img.shields.io/badge/No-Ads-green)




# Hakkında



Bu uygulama ile şunları yapabilirsiniz:
- 🔽 Dosyaları kendi yerel sunucunuzdan indirin
- ✏️ Sunucunuzdaki dosyaları anında yeniden adlandırın
- 🔼 Dosyaları herhangi bir tarayıcıdan doğrudan cihazınıza yükleyin

📡 Tüm işlemler **yerel ağınız** içinde tamamen **cihazınızda** gerçekleştirilir.  
🚫 KaplanDrive **herhangi bir harici sunucuya bağlanmaz** - verileriniz sizde kalır.

İster bir geliştirici, ister güçlü bir kullanıcı veya gizliliğe önem veren biri olun, Kaplandrive yerel dosya aktarımları için temiz ve verimli bir çözüm sunar.

Önemli Noktalar:
- Bilgisayarınızda barındırılan hızlı ve kararlı HTTP/HTTPS sunucusu
- Paylaşılan depolamayı yönetmek için tam destek (MANAGE_EXTERNAL_STORAGE izni)
- 100 açık kaynaklı ve reklamsız





## Sunucu Kurulumu*

*"**KaplanDrive**" veya *"A KaplanBedwars Orginal Team*" herhangi bir sunucu sağlamaz.*

 Uygulama'nın şu anda sadece "192.168.1.38" adresi için http desteği varıdr. Yani ***HTTPS*** sunucusu kurmanız gerekir. 
 
 Uygulama dosyaları; **Yeniden adlandırma, Karşıya dosya yükleme, Karşıdaki dosyayı indirme** işlermleri için sunucunuzda bazı yollar olmalıdır

 | Yol | İşlev
| :-------- | :------- 
| `/files`      | `Dosyaların JSON listesi` 
| `/upload`           | `Sunucuya dosya yüklemek için`
|     `/rename`          |`Sunucudan dosya yeniden isimlendirmek için`

### Örnek yol kodları


#### /files:


```js
app.get('/files', (req, res) => {
  fs.readdir('uploads', (err, files) => {
    if (err) return res.status(500).send('Dosyalar okunamadı');
    res.json({ files: files.map(filename => `/uploads/${filename}`) });
  });
});

```

Çıktısı Böyle Olmalıdır:

```json
{
  "files": [
    "/uploads/file_01.jpg",
    "/uploads/file_02.jpg",
    "/uploads/file_03.jpg",
    "/uploads/file_04.jpg",
    "/uploads/file_05.zip",
    "/uploads/file_06.apk",
    "/uploads/file_07.apk",
    "/uploads/file_08.jpg",
    "/uploads/file_09.jpg",
    "/uploads/file_10.jpg",
    "/uploads/file_11.mp4",
    "/uploads/file_12.mp4",
    "/uploads/file_13.docx",
    "/uploads/file_14.jpg",
    "/uploads/file_15.mp4",
    "/uploads/file_16.jpg",
    "/uploads/file_17.apk",
    "/uploads/file_18.jpg",
    "/uploads/file_19.jpg",
    "/uploads/file_20.jpg",
    "/uploads/file_21.jpg",
    "/uploads/file_22.png",
    "/uploads/file_23.png",
    "/uploads/file_24.png",
    "/uploads/file_25.png",
    "/uploads/file_26.png",
    "/uploads/file_27.png",
    "/uploads/file_28.png",
    "/uploads/file_29.mp4",
    "/uploads/file_30.mp4",
    "/uploads/file_31.docx",
    "/uploads/file_32.txt",
    "/uploads/file_33.webp",
    "/uploads/file_34.jpg",
    "/uploads/file_35.jpg",
    "/uploads/file_36.jpg",
    "/uploads/file_37.mp4",
    "/uploads/file_38.png",
    "/uploads/file_39.jpg"
  ]
}


```

#### /upload:

```js
app.post('/upload', upload.single('file'), (req, res) => {
  if (!req.file) {
    return res.status(400).send({ error: 'Dosya seçilmedi.' });
  }
  res.status(200).json({ url: `/uploads/${req.file.filename}` });
});
```


#### /rename:

```
app.post('/rename', (req, res) => {
  const { oldPath, newName } = req.body;

  // 1. Gelen oldPath'den "uploads/" kısmını çıkar (eğer varsa)
  const sanitizedOldPath = oldPath.replace(/^\/?uploads\//, ''); // "uploads/ilk.apk" → "ilk.apk"

  // 2. Güvenlik kontrolü (../ karakterlerini engelle)
  if (sanitizedOldPath.includes('..') || newName.includes('..')) {
    return res.status(400).json({ error: "Geçersiz dosya yolu!" });
  }

  // 3. Dosya yollarını oluştur
  const baseDir = path.join(__dirname, 'uploads');
  const absoluteOldPath = path.join(baseDir, sanitizedOldPath); // /proje_dizini/uploads/ilk.apk
  const absoluteNewPath = path.join(baseDir, path.dirname(sanitizedOldPath), newName);

  // 4. Dosya varlık kontrolü
  if (!fs.existsSync(absoluteOldPath)) {
    return res.status(404).json({ error: "Dosya bulunamadı!" });
  }

  // 5. Yeniden adlandırma işlemi
  fs.rename(absoluteOldPath, absoluteNewPath, (err) => {
    if (err) {
      console.error("Hata:", err);
      return res.status(500).json({ error: "Dosya işlenemedi!" });
    }
    res.json(newName);
  });


  ```
## Yazarlar ve Teşekkür

- [@KaplanBedwars](https://github.com/KaplanBedwars) tasarım ve geliştirme için.

  
