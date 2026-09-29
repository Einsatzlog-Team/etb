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
) {
    val widthDp: Int get() = (widthPx / density).toInt()
    val heightDp: Int get() = (heightPx / density).toInt()
}

enum class Store { PLAY, APP_STORE }

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
val PLAY_TABLET = DeviceSpec("play-tablet", 1620, 2880, 2f, Store.PLAY, "tenInchScreenshots")

/** 6.9" iPhone — the one iPhone set Apple scales down for every smaller model. */
val IOS_IPHONE_69 = DeviceSpec("ios-iphone-6.9", 1320, 2868, 3f, Store.APP_STORE)

// iOS ships iPhone-only (TARGETED_DEVICE_FAMILY = 1), so no 13" iPad set.
val ALL_DEVICES = listOf(PLAY_PHONE, PLAY_TABLET, IOS_IPHONE_69)

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
