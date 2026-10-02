# Einsatzlog

**A free, offline incident logbook (*Einsatztagebuch*) for fire departments.**

During an incident, one person documents every radio message: who called whom, what was said, and exactly when. In most volunteer fire departments this still happens on paper. Einsatzlog turns it into a fast, trustworthy digital record – on the phone or tablet the station already has.

- **An entry in seconds.** The timestamp is captured when the form opens and cannot be edited; corrections only go backwards (−1 / −5 min). Colour-coded message types (Funkspruch, Lagemeldung, Auftrag, Anforderung, Dokumentation) and call-sign suggestions learned from your own radio traffic.
- **A record you can trust.** Closing an incident makes the log read-only; reopening is written into the log automatically.
- **The report in one tap.** A print-ready PDF for the official file, any time during or after an incident.
- **Nothing leaves the device.** No account, no server, no ads, no tracking. Works offline. German and English, light and dark theme, touch targets sized for gloves.
- **Free, forever.** Every feature is free. The store builds offer an optional tip that supports development and unlocks nothing.

Website and devlog: [einsatzlog.de](https://einsatzlog.de) · Developer blog (architecture, decisions, code): [einsatzlog.de/en/blog](https://einsatzlog.de/en/blog/)

## Platforms

- **Android** – on Google Play
- **iOS** – iPhone, in App Store review

## How it's built

One Kotlin codebase, native on Android and iOS:

- **Kotlin Multiplatform + Compose Multiplatform** – every screen and all logic live in shared code; only the PDF renderer (Android `PdfDocument`, iOS CoreGraphics), the database location and a thin SwiftUI host are platform-specific.
- **Room (KMP)** with the bundled SQLite driver – offline storage on both platforms.
- **Atomic design** for the UI (particles → atoms → molecules → organisms → screens) in the shared `:ui` module.
- **Koin** for dependency injection; services sit behind interfaces (`SupportRepository`, `SuggestionProvider`, `LogbookExporter`, `CrashReporter`).
- **Purchases in their own module:** `:purchases-revenuecat` is the only code that knows RevenueCat. iOS and the Android `store` flavor (Google Play, optional tip) link it; the `foss` flavor doesn't, so its APK contains no proprietary code – no Play Billing, Play Services or Firebase.
- **Tooling in the repo:** `:screenshots` renders the store screenshots headlessly from the real Compose screens; `video/` records the demo video with Maestro on Android and iOS.

## Build

Requirements: JDK 21, Android SDK; a Mac with Xcode for iOS.

```bash
./gradlew :composeApp:assembleStoreDebug       # Android, store flavor
./gradlew :composeApp:assembleFossDebug        # Android, FOSS flavor
./gradlew :composeApp:testStoreDebugUnitTest   # unit tests
./gradlew :screenshots:screenshots             # store screenshots → screenshots/build/store
```

iOS: open `iosApp/iosApp.xcodeproj` in Xcode and run.

**In-app purchase keys are not part of the repository.** Without them the app builds and runs normally; the support screen shows that purchases aren't available. To enable purchases, add the RevenueCat public SDK keys to the gitignored `local.properties` (or pass `-Prevenuecat.androidKey=…`, or set `REVENUECAT_ANDROID_KEY` / `REVENUECAT_IOS_KEY`):

```properties
revenuecat.androidKey=goog_…
revenuecat.iosKey=appl_…
```

A RevenueCat Test Store key (`test_…`) works for local testing; release builds refuse it. Release signing reads a gitignored `keystore.properties`.

## Branches

The repository follows **gitflow**:

| Branch | Purpose |
|---|---|
| `main` | Released versions only. Every release is tagged (`v0.1.4`, …). |
| `develop` | The current state of the app – stable, the base for all new work. |
| `feature/*` | New features, branched from and merged back into `develop` (e.g. `feature/tablet-layout`). |
| `release/*` | Release preparation, branched from `develop`, merged into `main` and back into `develop`. |
| `hotfix/*` | Urgent fixes on a released version, branched from `main`. |

## Roadmap

- Optional tip on iOS
- Tablet layouts (list, logbook and a permanent entry pane side by side)
- The vehicle and crew overview from the original sketches, entry templates
- Private, on-device suggestions that learn a station's radio language – without data leaving the device

## Contributing

Issues and pull requests are welcome – please branch from `develop`. Read the [Code of Conduct](./CODE_OF_CONDUCT.md) first. Domain terms (Einsatz, Funkspruch, Lagemeldung …) stay German in every language – they're the words used on the radio.

## License

[MIT](./LICENSE)
