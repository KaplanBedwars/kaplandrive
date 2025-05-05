const express = require('express');
const multer = require('multer');
const fs = require('fs');
const path = require('path');
const cors = require('cors');

const app = express();
const PORT = 8080;


/*
"-----------------------[A KaplanBedwars Original Team (AKOT) kaplandrive example server]-----------------------
Created at 5.05.2025
License: AGPLv3

Bu kod, örnek sunucu olarak yazılmış olup, güncelleme almayacaktır. Sunucu sorunsuz çalışır, ancak sorunsuz çalıştığı iddia edilmez."

-----------------------[A KaplanBedwars Original Team (AKOT) kaplandrive example server]-----------------------
*/ 


// Middleware
app.use(cors());
app.use(express.json());
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

// Dosya yükleme ayarları
const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    cb(null, 'uploads/');
  },
  filename: (req, file, cb) => {
    cb(null, file.originalname);
  },
});
const upload = multer({ storage });

// /files → dosya listesini JSON olarak gönderir
app.get('/files', (req, res) => {
  fs.readdir('uploads', (err, files) => {
    if (err) return res.status(500).send('Dosyalar okunamadı');
    res.json({ files: files.map(filename => `/uploads/${filename}`) });
  });
});

// /upload → dosya yükler
app.post('/upload', upload.single('file'), (req, res) => {
  if (!req.file) {
    return res.status(400).send({ error: 'Dosya seçilmedi.' });
  }
  res.status(200).json({ url: `/uploads/${req.file.filename}` });
});

// /rename → dosya yeniden adlandırır
app.post('/rename', (req, res) => {
  const { oldPath, newName } = req.body;

  const sanitizedOldPath = oldPath.replace(/^\/?uploads\//, '');
  if (sanitizedOldPath.includes('..') || newName.includes('..')) {
    return res.status(400).json({ error: "Geçersiz dosya yolu!" });
  }

  const baseDir = path.join(__dirname, 'uploads');
  const absoluteOldPath = path.join(baseDir, sanitizedOldPath);
  const absoluteNewPath = path.join(baseDir, path.dirname(sanitizedOldPath), newName);

  if (!fs.existsSync(absoluteOldPath)) {
    return res.status(404).json({ error: "Dosya bulunamadı!" });
  }

  fs.rename(absoluteOldPath, absoluteNewPath, (err) => {
    if (err) {
      console.error("Hata:", err);
      return res.status(500).json({ error: "Dosya işlenemedi!" });
    }
    res.json({ message: "Dosya adı değiştirildi", newName });
  });
});

app.listen(PORT, () => {
  console.log(`📡 KaplanDrive Sunucusu http://localhost:${PORT} adresinde çalışıyor`);
});
