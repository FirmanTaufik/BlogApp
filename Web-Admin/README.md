# Blog Admin Firebase Tanpa Build Tool

Project ini memakai HTML, CSS, dan JavaScript biasa. Cover image dan gambar label memakai URL eksternal, lalu data artikel disimpan ke Firestore.

## File utama

- `index.html`
- `style.css`
- `app.js`
- `firebase-config.js`

## Struktur data Firestore

Collection: `posts`

Field yang disimpan:

- `defaultLocale`
- `locales`
- `contentFormat`
- `views`
- `labels`
- `labelIds`
- `coverImageUrl`
- `imageName`
- `createdAt`
- `updatedAt`

Collection: `labels`

Field yang disimpan:

- `defaultLocale`
- `names`
- `imageUrl`
- `imageName`
- `createdAt`
- `updatedAt`

Collection: `locales`

Field yang disimpan:

- `code`
- `name`
- `createdAt`
- `updatedAt`

Document: `settings/admob`

Field yang disimpan:

- `enabled`
- `showBanner`
- `showInterstitial`
- `showAppOpen`
- `appId`
- `bannerAdUnitId`
- `interstitialAdUnitId`
- `interstitialInterval`
- `appOpenAdUnitId`
- `createdAt`
- `updatedAt`

Document: `homeSlider/main`

Field yang disimpan:

- `images`
- `createdAt`
- `updatedAt`

## Cara pakai

1. Buka file `firebase-config.js`
2. Isi semua nilai Firebase project Anda
3. Pastikan Anda sudah membuat Firestore Database
4. Isi cover image dan gambar label memakai URL gambar
5. Buka `index.html` di browser

## Bahasa Dinamis

- Locale tidak lagi hardcoded
- Tambahkan bahasa langsung dari form `Tambah Bahasa`
- Setelah locale disimpan, form artikel dan label akan otomatis membuat field baru untuk locale tersebut
- Contoh kode locale: `id`, `en`, `fr`, `pt-br`

## Mode konten

Field `contentFormat` akan disimpan sebagai:

- `text` untuk plain text
- `html` untuk markup HTML

## Multi bahasa

Post akan disimpan seperti ini:

```json
{
  "defaultLocale": "id",
  "locales": {
    "id": {
      "title": "Judul Indonesia",
      "content": "Isi bahasa Indonesia"
    },
    "fr": {
      "title": "Titre Francais",
      "content": "Contenu en francais"
    }
  }
}
```

Label akan disimpan seperti ini:

```json
{
  "defaultLocale": "id",
  "names": {
    "id": "Teknologi",
    "fr": "Technologie"
  }
}
```

## Label

- Label disimpan di collection `labels`
- Form artikel bisa memilih lebih dari satu label
- Setiap label bisa memiliki gambar sendiri lewat URL eksternal
- Nama label akan menyesuaikan locale artikel saat ditampilkan

## AdMob

- Menu `AdMob` menyimpan konfigurasi ke document `settings/admob`
- Aplikasi mobile membaca banner, interstitial, dan app open ad unit ID dari Firestore
- `interstitialInterval` menentukan setiap berapa klik artikel dari home interstitial muncul sebelum detail dibuka
- Gunakan tombol `Isi Test ID` untuk mengisi ID test Google AdMob saat development
- Untuk Android, `appId` tetap harus cocok dengan meta-data `com.google.android.gms.ads.APPLICATION_ID` di AndroidManifest saat app dirilis

## Home Banner Slider

- Menu `Slider` menyimpan daftar URL gambar ke document `homeSlider/main`
- Isi satu URL gambar per baris
- Aplikasi mobile membaca field `images` dan menampilkannya di banner slider Home sesuai urutan input

## Konfigurasi Firebase

Ambil config dari Firebase Console:

- Project settings
- General
- Your apps

Lalu tempel ke `firebase-config.js`

## Catatan penting

- Karena ini app client-side, Firebase config memang terlihat di browser. Itu normal untuk Firebase web app.
- Yang wajib diamankan adalah Firestore Rules.
- Jika browser membatasi akses saat file dibuka lewat `file://`, jalankan dengan static server ringan. Tetapi untuk target sederhana, struktur project ini sudah tidak membutuhkan Node atau npm.

## Langkah berikutnya

- Tambahkan login admin dengan Firebase Authentication
- Tambahkan fitur edit dan hapus artikel
- Tambahkan halaman public untuk menampilkan artikel blog
