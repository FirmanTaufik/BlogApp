# Firebase Firestore Setup

Project ini sudah disiapkan untuk memakai Firestore dari `commonMain` melalui `dev.gitlive:firebase-firestore`.

## Alur kode

- `FirestoreGuideService` membaca dokumen Firestore `guides/featured`
- `MainRepository` mengubah data itu menjadi `UIState`
- `MainViewModel` memuat data dengan coroutine
- `HomeScreen` menampilkan hasilnya

## Android

1. Daftarkan app Android dengan package `com.time.yourguideapp` di Firebase Console.
2. Unduh `google-services.json`.
3. Letakkan file itu di folder `composeApp/`.

Catatan:
Plugin `com.google.gms.google-services` hanya akan di-apply jika file `composeApp/google-services.json` ada, supaya repo ini tetap bisa di-build sebelum konfigurasi Firebase selesai.

## iOS

1. Daftarkan app iOS di Firebase Console.
2. Unduh `GoogleService-Info.plist`.
3. Tambahkan file itu ke project Xcode `iosApp` dan pastikan target iOS ikut tercentang.
4. Di Xcode, buka `File > Add Packages`.
5. Tambahkan package `https://github.com/firebase/firebase-ios-sdk`.
6. Pilih minimal `FirebaseCore` dan `FirebaseFirestore`.

Catatan:
Kode di `iosApp/iosApp/iOSApp.swift` sudah menyiapkan `FirebaseApp.configure()` dengan `UIApplicationDelegateAdaptor`.

## Dokumen contoh Firestore

Collection:
`guides`

Document:
`featured`

Contoh isi:

```json
{
  "title": "Explore Bangkok",
  "summary": "Panduan singkat tempat yang layak dikunjungi minggu ini.",
  "category": "Travel",
  "highlights": [
    "Wat Arun saat sore",
    "Talad Noi untuk jalan kaki",
    "Charoenkrung untuk kuliner"
  ]
}
```
