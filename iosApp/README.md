# iosApp — setup notes (Mac only)

The Xcode shell was carried over from the predecessor project and adapted (bundle id `de.einsatzlog.app`, name Einsatzlog, versions via `Configuration/Config.xcconfig`). iOS cannot be built on the Linux dev box — first build happens on the Mac:

1. Set your `TEAM_ID` in `Configuration/Config.xcconfig` (empty on purpose).
2. Open `iosApp.xcodeproj`, check Signing & Capabilities picks up team + bundle id.
3. **Add `iosApp/PrivacyInfo.xcprivacy` to the iosApp target** (File → Add Files…) — it exists on disk but the carried-over pbxproj doesn't reference it yet. App Store submission needs it.
4. The build phase runs `./gradlew :composeApp:embedAndSignAppleFrameworkForXcode` (needs JDK 21 — the script resolves it via `/usr/libexec/java_home -v 21`; adjust if the Mac has a different JDK layout).
5. Run on a simulator: create an Einsatz, kill the app, relaunch → it must still be there (Room/commonMain persistence check, spec 001).
6. Replace the carried-over `Assets.xcassets/AppIcon.appiconset/Logo.png` with the Einsatzlog icon once it exists (must have **no alpha channel**).
