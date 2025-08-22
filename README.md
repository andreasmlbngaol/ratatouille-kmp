# moprog

Proyek semester untuk mata kuliah Mobile Programming. Repository ini digunakan sebagai monorepo untuk:
- Backend (API/Server)
- Aplikasi Mobile berbasis Kotlin Compose Multiplatform

Catatan versi tool yang disarankan:
- Java SDK 21
- Kotlin API 2.2
- Android SDK target 36
---

## Strategi Branch Git

Kita menggunakan tiga lapis branch:

- `main`
  - Branch produksi (release).
  - Hanya menerima PR dari `development` setelah lulus QA/UAT.
- `development`
  - Integrasi utama pengembangan harian.
  - Menerima PR dari branch fitur (`features/xxx`).
- `features/xxx`
  - Branch tempat masing-masing kontributor bekerja pada satu fitur.
  - Format penamaan: `features/<deskripsi-singkat>` (contoh: `features/auth-login`, `features/profile-edit`).