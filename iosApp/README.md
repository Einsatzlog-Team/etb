# iosApp — setup notes (Mac only)

The Xcode shell was carried over from the predecessor project and adapted (bundle id `de.einsatzlog.app`, name Einsatzlog, versions via `Configuration/Config.xcconfig`). iOS cannot be built on the Linux dev box — first build happens on the Mac:

1. **Leave `TEAM_ID` in `Configuration/Config.xcconfig` empty.** The bundle id is built as
   `${BUNDLE_ID}${TEAM_ID}`, so a value would turn it into `de.einsatzlog.app<TEAMID>` and no longer
   match App Store Connect. The team itself is set in the project (Signing & Capabilities).
2. Open `iosApp.xcodeproj`, check Signing & Capabilities picks up team + bundle id.
3. `iosApp/PrivacyInfo.xcprivacy` is part of the iosApp target (required for App Store submission).
4. The build phase runs `./gradlew :composeApp:embedAndSignAppleFrameworkForXcode` (needs JDK 21 — the script uses `jenv javahome` when jenv is installed, otherwise `/usr/libexec/java_home -v 21`).
5. Run on a simulator: create an Einsatz, kill the app, relaunch → it must still be there (Room/commonMain persistence check, spec 001).
6. `Assets.xcassets/AppIcon.appiconset/Logo.png` is the Einsatzlog icon, flattened on white — App Store icons must have **no alpha channel**.
7. Per release, bump `MARKETING_VERSION` / `CURRENT_PROJECT_VERSION` in `Configuration/Config.xcconfig` together with the Android version in `composeApp/build.gradle.kts`.
8. RevenueCat: put `revenuecat.iosKey=appl_…` in the repo's gitignored `local.properties` (without it the support screen shows "unavailable").
