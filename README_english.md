
---

# A KaplanBedwars Original team: KaplanDrive

#### Roadmate:

[Privacy Policy](https://github.com/KaplanBedwars/kaplandrive/blob/main/TERMS.md)

### Badges:

[![Original](https://github.com/KaplanBedwars/KaplanBedwars/blob/main/q\(1\).png)](https://choosealicense.com/licenses/mit/)
[![GPLv3 License](https://img.shields.io/badge/License-GPL%20v3-yellow.svg)](https://opensource.org/licenses/)
[![GPLv3 License](https://img.shields.io/badge/Language-Java-blue)](https://opensource.org/licenses/)
[![GPLv3 License](https://img.shields.io/badge/Platform-Android-Green)](https://opensource.org/licenses/)
![A KaplanBedwars Original](https://img.shields.io/badge/A_KaplanBedwars_Original-%E2%AD%90-orange)
![Status](https://img.shields.io/badge/status-stable-brightgreen)
![Mod](https://img.shields.io/badge/type-Android_App-red)
![Made with ❤️](https://img.shields.io/badge/Made_with-KaplanBedwars%E2%9D%A4-red)
![Not a Fork](https://img.shields.io/badge/100%25-Original-orange)
![No Ads](https://img.shields.io/badge/No-Ads-green)

---

# About

With this app, you can:

* 🔽 Download files from your own local server
* ✏️ Instantly rename files on your server
* 🔼 Download the files from your server to your device

📡 All operations are performed **entirely on your device** within your **local network**.
🚫 KaplanDrive **does not connect to any external servers** – your data stays with you.

Whether you're a developer, a power user, or someone who values privacy, KaplanDrive offers a clean and efficient solution for local file transfers.

Highlights:

* Fast and stable HTTP/HTTPS server hosted on your computer
* Full support for managing shared storage (MANAGE\_EXTERNAL\_STORAGE permission)
* 100% open source and ad-free

---

## Server Setup\*

*"**KaplanDrive**" or *"A KaplanBedwars Original Team*" does not provide any server.*

The application currently only supports HTTP for the address "192.168.1.38". So you must set up an ***HTTPS*** server.

For **Renaming, Uploading, and Downloading** files, your server should have specific paths available.

| Path      | Function                            |
| :-------- | :---------------------------------- |
| `/files`  | `JSON list of files`                |
| `/upload` | `For uploading files to the server` |
| `/rename` | `For renaming files on the server`  |

### Example path code

#### /files:

```js
app.get('/files', (req, res) => {
  fs.readdir('uploads', (err, files) => {
    if (err) return res.status(500).send('Files could not be read');
    res.json({ files: files.map(filename => `/uploads/${filename}`) });
  });
});
```

Output should look like this:

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
    return res.status(400).send({ error: 'No file selected.' });
  }
  res.status(200).json({ url: `/uploads/${req.file.filename}` });
});
```

#### /rename:

```js
app.post('/rename', (req, res) => {
  const { oldPath, newName } = req.body;

  // 1. Remove "uploads/" from oldPath (if present)
  const sanitizedOldPath = oldPath.replace(/^\/?uploads\//, ''); // "uploads/first.apk" → "first.apk"

  // 2. Security check (prevent ../ characters)
  if (sanitizedOldPath.includes('..') || newName.includes('..')) {
    return res.status(400).json({ error: "Invalid file path!" });
  }

  // 3. Build file paths
  const baseDir = path.join(__dirname, 'uploads');
  const absoluteOldPath = path.join(baseDir, sanitizedOldPath);
  const absoluteNewPath = path.join(baseDir, path.dirname(sanitizedOldPath), newName);

  // 4. Check file existence
  if (!fs.existsSync(absoluteOldPath)) {
    return res.status(404).json({ error: "File not found!" });
  }

  // 5. Perform rename
  fs.rename(absoluteOldPath, absoluteNewPath, (err) => {
    if (err) {
      console.error("Error:", err);
      return res.status(500).json({ error: "File could not be processed!" });
    }
    res.json(newName);
  });
});
```

---

## Authors and Thanks

* [@KaplanBedwars](https://github.com/KaplanBedwars) for design and development.

---

