# BlogApp

Repository ini berisi dua project:

- `Web-Admin`: panel admin berbasis HTML, CSS, dan JavaScript untuk mengelola konten Firestore.
- `YourGuideApp`: aplikasi mobile Kotlin Multiplatform / Compose yang membaca konten dari Firestore.

## Fitur Utama

- CRUD artikel dari Web Admin.
- Artikel bilingual/multibahasa melalui collection `locales`.
- Artikel hanya tampil di aplikasi jika punya konten untuk bahasa aktif.
- Label artikel dikelola dari Web Admin.
- Home banner slider dikelola dari Web Admin dan disimpan di root collection `homeSlider`.
- Konfigurasi AdMob dikelola dari Web Admin dan disimpan di `settings/admob`.
- Banner AdMob tampil di root main dan detail, tetapi tidak di splash screen.
- Interstitial AdMob muncul dari klik item home ke detail sesuai interval dari Web Admin.
- App open AdMob dibaca dari Firestore.
- Bookmark/love detail tersimpan ke saved list.

## Struktur Project

```text
BlogApp/
├── Web-Admin/
│   ├── index.html
│   ├── style.css
│   ├── app.js
│   ├── firebase-config.js
│   └── README.md
└── YourGuideApp/
    ├── composeApp/
    ├── iosApp/
    ├── gradle/
    └── README.md
```

## Firestore

Collection dan document utama:

- `posts`: data artikel.
- `labels`: data label artikel.
- `locales`: daftar bahasa aktif.
- `homeSlider/main`: daftar URL gambar banner slider home.
- `settings/admob`: konfigurasi AdMob.
- `users/{userId}/bookmarks`: saved list/bookmark user.

## Web Admin

Web Admin dapat dibuka langsung dari:

```text
Web-Admin/index.html
```

Konfigurasi Firebase ada di:

```text
Web-Admin/firebase-config.js
```

Pastikan Firestore Rules mengizinkan akses yang sesuai untuk kebutuhan admin. Karena Web Admin berjalan client-side, Firebase config memang terlihat di browser; keamanan utama tetap berada di Firestore Rules.

## Mobile App

Build Android debug:

```shell
cd YourGuideApp
./gradlew :composeApp:assembleDebug
```

Android manifest memakai test AdMob app id untuk development. Ad unit ID banner, interstitial, interval interstitial, dan app open diambil dari Firestore melalui konfigurasi yang diinput di Web Admin.

## Catatan Konten

Artikel travel guide yang sudah dibuat disimpan langsung ke Firestore dan tersedia dalam locale `id` dan `en`. Jika user memilih bahasa Indonesia, aplikasi hanya menampilkan post yang punya konten `id`; jika memilih English, aplikasi hanya menampilkan post yang punya konten `en`.
