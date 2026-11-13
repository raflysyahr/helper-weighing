# Helper Weighing

Aplikasi Android (Java) untuk membantu operator batch weighing: hitung target timbang per batch,
rencana pembagian batch, cek timbangan terhadap toleransi, dan tally timbangan.

- App ID: `com.helper.weighing`
- minSdk 26, targetSdk 34, Java 8, AGP 8.2.2, Gradle 8.5 (di CI)

## Build lewat GitHub Actions
Push ke `main` atau jalankan manual (Actions > Android CI > Run workflow). APK ada di Artifacts.
Tag `v1.0.0` otomatis membuat GitHub Release berisi APK + AAB.

Untuk tanda tangan tetap, isi Secrets repo: `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`.
Tanpa secrets, CI memakai keystore sementara.
