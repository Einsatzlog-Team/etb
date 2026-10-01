# Plan: Einsatzlog on F-Droid (plain FOSS build)

**Goal:** publish the existing `foss` flavor on F-Droid – a build that contains no proprietary
code at all, builds from source on F-Droid's servers, and needs no Google services. It serves
stations with de-Googled phones or tablets without the Play Store, and makes the "nothing
leaves the device" promise verifiable.

**State: 2026-10-01, branch `release/0.1.2`, app version 0.1.5 (35).**

---

## 1. Where we stand (verified)

The `foss` flavor already exists (`applicationIdSuffix = ".foss"`) and selects
`NoopSupportRepository` at runtime. **But it is not F-Droid-ready**, because the purchase SDK
is a `commonMain` dependency and therefore ends up in both flavors.

`./gradlew :composeApp:dependencies --configuration fossReleaseRuntimeClasspath` and the dex of
`composeApp-foss-release.apk` contain:

| Non-free / Google code in the foss APK | Comes from |
|---|---|
| `com.android.billingclient:billing` (Play Billing, proprietary) | RevenueCat `purchases-kmp` |
| `com.google.android.gms:*` incl. `play-services-ads-identifier`, `play-services-auth-blockstore` | RevenueCat |
| `com.google.firebase:*` encoders, `com.google.android.datatransport` | RevenueCat (transitive) |
| `com.revenuecat.purchases:*` (MIT, but pulls in the above) | `commonMain` dependency |

Everything else on the classpath is free software (AndroidX, JetBrains/Compose Multiplatform,
Koin, AboutLibraries, Kotlin/kotlinx, Gson, Guava, Tink, …).

RevenueCat references in code: `commonMain/support/RevenueCatConfig.kt`,
`commonMain/support/RevenueCatSupportRepository.kt`, `commonMain/di/AppModule.kt`,
`androidMain/EinsatzlogApplication.kt`, `androidMain/support/RevenueCatConfig.android.kt`,
`iosMain/MainViewController.kt`, `iosMain/support/RevenueCatConfig.ios.kt`.

Already in our favour: no network code in the app itself, no analytics, the store screenshots
are rendered by the `:screenshots` module in the fastlane layout, the code is MIT on GitHub.

---

## 2. Work packages

### WP1 – Compile purchases out of the FOSS build (the real work)

Move everything RevenueCat-specific out of `commonMain` into its own module, and only link it
where purchases exist.

1. New KMP module **`:purchases-revenuecat`** (targets `android`, `iosArm64`,
   `iosSimulatorArm64`) containing `RevenueCatConfig`, `RevenueCatSupportRepository`, the
   `purchases-kmp` dependency and the `generateRevenueCatKeys` task. It exposes one Koin
   module, e.g. `revenueCatSupportModule`.
2. `:composeApp` `commonMain` keeps only the `SupportRepository` interface and
   `NoopSupportRepository` (already in `:ui`). `appModule()` no longer knows RevenueCat.
3. Wiring per platform:
   - **iOS:** `iosMain` depends on `:purchases-revenuecat` and loads its module (unchanged
     behaviour, incl. the `IOS_PURCHASES_ENABLED` switch).
   - **Android store:** `storeImplementation(project(":purchases-revenuecat"))`; a
     store-flavor source set provides the Koin module.
   - **Android foss:** a foss-flavor source set provides `NoopSupportRepository` and **hides
     the support entry** (the `onOpenSupport = null` pattern from iOS 0.1.4).
   - Compile-time selection replaces the runtime check `BuildConfig.FLAVOR == "store"`.
4. **Spike first (½ day):** confirm that Kotlin Multiplatform with `com.android.application`
   gives us flavor-specific Kotlin source sets (`androidStore` / `androidFoss`) that can see
   flavor-specific dependencies. Fallback: plain Android flavor source dirs
   (`src/store/kotlin`, `src/foss/kotlin`) registered via `android.sourceSets`.

**Acceptance criteria**
- [ ] `fossReleaseRuntimeClasspath` contains no `com.revenuecat`, `com.android.billingclient`,
      `com.google.android.gms`, `com.google.firebase`, `com.google.android.datatransport`
- [ ] the foss APK's dex contains none of these packages (same `strings` check as above)
- [ ] an Exodus Privacy scan of the foss APK shows **0 trackers**
- [ ] store flavor and iOS unchanged: purchase, restore and thank-you still work (sandbox / Test Store)
- [ ] foss build shows no heart and no "Support" entry in settings

### WP2 – Build like F-Droid does

F-Droid builds on Linux, from a git tag, without Xcode and without our `local.properties`.

- [ ] `./gradlew :composeApp:assembleFossRelease` succeeds on Linux in a clean checkout,
      ideally in the fdroidserver container – iOS targets must be skipped, not fail
      (`kotlin.native.ignoreDisabledTargets=true` if needed), and no Kotlin/Native toolchain
      download should be required for the Android task.
- [ ] No prebuilt binaries in the repo; all dependencies from Maven Central / Google Maven.
- [ ] **Version numbers readable without running Gradle:** today `versionCode`/`versionName`
      come from variables (`gitVersionCode`). F-Droid's update checker works best with literal
      values or a simple `version.properties`. Move them there (shared with the iOS xcconfig bump).
- [ ] Decide signing: phase 1 F-Droid signs with its own key (default). Phase 2 (optional):
      reproducible builds so F-Droid can ship our signature.
- [ ] Keep the application ID `de.einsatzlog.app.foss` – it's a separate app from the Play
      version and avoids signature conflicts on devices that have both.

### WP3 – Store metadata in the repo (fastlane layout)

F-Droid reads listing texts and images from the source repository:

```text
fastlane/metadata/android/
  de-DE/  title.txt  short_description.txt  full_description.txt
          changelogs/<versionCode>.txt
          images/icon.png  images/featureGraphic.png  images/phoneScreenshots/*.png
  en-US/  (same)
```

- [ ] Texts from the store listing, adapted for FOSS: no in-app purchase, emphasise
      "no Google services, no tracking, open source".
- [ ] Screenshots: the `:screenshots` module already writes the Play layout – add a
      F-Droid/foss target that renders **without** the support entry (as for the iOS 0.1.4 set)
      and copies into `fastlane/metadata/android/<locale>/images/phoneScreenshots/`.
- [ ] Icon 512×512 and the feature graphic (already designed in `store-banner/`).
- [ ] A changelog file per release (`changelogs/<versionCode>.txt`).

### WP4 – Inclusion request to F-Droid

- [ ] Tag a release on the public repo (e.g. `v0.1.6`), including all of the above.
- [ ] Merge request to `fdroiddata` with `metadata/de.einsatzlog.app.foss.yml`, roughly:

```yaml
Categories:
  - <pick from F-Droid's current category list, e.g. Writing or Office>
License: MIT
AuthorName: Einsatzlog-Team
WebSite: https://einsatzlog.de
SourceCode: https://github.com/Einsatzlog-Team/etb
IssueTracker: https://github.com/Einsatzlog-Team/etb/issues
Changelog: https://github.com/Einsatzlog-Team/etb/releases

AutoName: Einsatzlog

RepoType: git
Repo: https://github.com/Einsatzlog-Team/etb.git

Builds:
  - versionName: 0.1.6
    versionCode: <versionCode>
    commit: v0.1.6
    subdir: composeApp
    gradle:
      - foss

AutoUpdateMode: Version
UpdateCheckMode: Tags
CurrentVersion: 0.1.6
CurrentVersionCode: <versionCode>
```

- [ ] Optionally a `Donate:` link (e.g. Liberapay/Open Collective) – the F-Droid way to support
      the project instead of an in-app purchase.
- [ ] Respond to reviewer questions; expect days to a few weeks until merge, then 1–3 days
      until the first build appears in the F-Droid client.

### WP5 – Keep it working

- [ ] CI job: build `assembleFossRelease` and fail if any of the forbidden packages appears in
      the dependency tree (turns the WP1 acceptance check into a guard).
- [ ] Release checklist: every tag bumps versions, adds changelogs, re-renders screenshots.
- [ ] README and website: link the F-Droid listing next to Google Play and the App Store.

---

## 3. Order and effort (rough)

| Step | Effort |
|---|---|
| WP1 spike (flavor source sets in KMP) | ½ day |
| WP1 module split + foss UI + checks | 1–2 days |
| WP2 Linux/F-Droid build, versions in a file | ½–1 day |
| WP3 fastlane metadata + foss screenshots | ½ day |
| WP4 tag + fdroiddata merge request | ½ day, then F-Droid's review time |
| WP5 CI guard | ½ day |

**Risks:** KMP flavor source sets (spike first); Kotlin/Native behaviour on a Linux build
server; F-Droid reviewers asking about the KMP/iOS parts of the repo (answer: Android-only
task, iOS targets disabled on Linux).

**Definition of done:** Einsatzlog (FOSS) installable from the F-Droid client, built by F-Droid
from a public tag, 0 trackers, no Google libraries, same features as the store build minus the tip.
