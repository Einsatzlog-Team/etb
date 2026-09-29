# Einsatzlog

**A digital operations logbook (Einsatztagebuch) for fire departments — free forever, offline-first, built in public.**

Roughly a million volunteer firefighters in Germany still document incident radio traffic on paper. Einsatzlog digitizes the operational record: create an Einsatz, log radio messages in seconds under stress, and export the legally relevant incident report as PDF. Data stays on the device.

- 📱 Android + iOS (Kotlin Multiplatform / Compose Multiplatform)
- 🔒 Offline-first, privacy-respecting — your data never leaves the device (optional crash reports in the store build)
- 🆓 Free forever, funded by voluntary supporter donations
- 🌍 German + English
- 🔓 Open source (MIT) — from v0.2 the app is developed in the open in this repo

## Origin & history — RevenueCat Shipaton 2026

**Einsatzlog started as a prototype that was developed privately over the last ten months.** It has now
gone open source and public: **v0.1.2 is a code-only snapshot** of that prototype, imported here as a
single commit *without* the private commit history (which contained personal setup and was never meant
to be published). It is released as the first store version inside the
[Shipaton 2026](https://revenuecat-shipaton-2026.devpost.com/) window.

**From v0.2 on, the successor app is developed in the open in this repository** — signed commits and
specs before code — reusing only the parts of v0.1.2 we choose to keep and documenting
each reuse-or-rewrite decision.

## Status

🏗 **Feature-complete v1, heading into its first store release.** All five vertical slices are built for
Android + iOS in German and English: Einsatz CRUD, sub-10-second quick entry, a supporter ("show
support") screen powered by RevenueCat, PDF export, and settings + compliance.

## Build

- Android: `./gradlew assembleStoreDebug` (Play flavor) · `./gradlew assembleFossDebug` (no proprietary SDKs)
- iOS: open `iosApp/iosApp.xcodeproj` in Xcode (needs a Mac)
- Tests: `./gradlew :composeApp:testStoreDebugUnitTest`
- Release signing reads a gitignored `keystore.properties`; RevenueCat uses *public* SDK keys only.
- RevenueCat keys are not in the repo. Without them the build works and the support screen shows
  "unavailable". To enable purchases, add to the gitignored `local.properties` (or pass
  `-Prevenuecat.androidKey=…` / set `REVENUECAT_ANDROID_KEY`):
  ```properties
  revenuecat.androidKey=goog_…
  revenuecat.iosKey=appl_…
  ```
  A Test Store key (`test_…`) works for local testing; release builds refuse it.

## License

MIT — see [LICENSE](LICENSE).
