package de.einsatzlog.screenshots

/**
 * Store image specifications, verified against the Play and App Store
 * requirements current in August 2026.
 *
 * The pixel sizes are not advisory. App Store Connect rejects a file whose
 * dimensions are off by a single pixel, and Play rejects anything past a 2:1
 * ratio — which is why [PLAY_PHONE] is 1080x2160 and not the 1080x2400 a
 * modern 20:9 handset screenshot would give you.
 *
 * [density] is what turns a dp layout into those exact pixels: a 6.9" iPhone
 * is 440x956 pt at @3x, so rendering 440x956 dp at density 3 lands precisely
 * on 1320x2868.
 */
data class DeviceSpec(
    val id: String,
    val widthPx: Int,
    val heightPx: Int,
    val density: Float,
    val store: Store,
    /** Sub-directory Play expects per form factor; unused for the App Store. */
    val playFolder: String? = null,
    /** Renders [TABLET_SHOTS] (the adaptive tablet layout) instead of the phone set. */
    val tablet: Boolean = false,
) {
    val widthDp: Int get() = (widthPx / density).toInt()
    val heightDp: Int get() = (heightPx / density).toInt()
}

enum class Store(
    /** Whether this store's current release has the support screen / in-app purchases. */
    val supportEnabled: Boolean,
) {
    PLAY(supportEnabled = true),

    /** Must match MainViewController.IOS_PURCHASES_ENABLED (false for 0.1.4, true from 0.1.5). */
    APP_STORE(supportEnabled = true),

    /** Devpost gallery: shows the Android release, which has the purchase. */
    DEVPOST(supportEnabled = true),
}

/**
 * Phone at exactly 9:16 (1440/2560), which satisfies every published version of
 * Play's rule at once — both the "max side no more than twice the min" limit
 * and the stricter 16:9/9:16 requirement in `docs/STORE-SUBMISSION.md`. At
 * density 3 that is 480x853 dp, a normal modern handset layout.
 *
 * Note this is why a raw device capture will not do: today's 20:9 phones
 * produce 1080x2400, which Play rejects outright.
 */
val PLAY_PHONE = DeviceSpec("play-phone", 1440, 2560, 3f, Store.PLAY, "phoneScreenshots")

/** 10" tablet, also exactly 9:16 — 810x1440 dp at density 2. */
val PLAY_TABLET = DeviceSpec("play-tablet", 1620, 2880, 2f, Store.PLAY, "tenInchScreenshots", tablet = true)

/** 10" tablet in landscape, 1440x810 dp at density 2 – the expanded width class (three panes). */
val PLAY_TABLET_LANDSCAPE =
    DeviceSpec("play-tablet-landscape", 2880, 1620, 2f, Store.PLAY, "tenInchScreenshots", tablet = true)

/** 6.9" iPhone — the one iPhone set Apple scales down for every smaller model. */
val IOS_IPHONE_69 = DeviceSpec("ios-iphone-6.9", 1320, 2868, 3f, Store.APP_STORE)

/** 6.5" iPhone (428x926 pt @3x) — App Store Connect asks for this tier when the 6.9" slot is not used. */
val IOS_IPHONE_65 = DeviceSpec("ios-iphone-6.5", 1284, 2778, 3f, Store.APP_STORE)

/** Devpost requires at least one 1179x2556 screenshot without a device frame (393x852 pt @3x). */
val DEVPOST_PHONE = DeviceSpec("devpost", 1179, 2556, 3f, Store.DEVPOST)

// iOS ships iPhone-only (TARGETED_DEVICE_FAMILY = 1), so no 13" iPad set.
val ALL_DEVICES = listOf(PLAY_PHONE, PLAY_TABLET, PLAY_TABLET_LANDSCAPE, IOS_IPHONE_69, IOS_IPHONE_65, DEVPOST_PHONE)

/**
 * Play and App Store Connect both key listings by these codes. They also
 * select which Compose resource bundle renders — `values/` is German (the
 * primary listing), `values-en/` is English.
 */
data class LocaleSpec(val storeCode: String, val javaTag: String)

val ALL_LOCALES = listOf(
    LocaleSpec("de-DE", "de"),
    LocaleSpec("en-US", "en"),
)
