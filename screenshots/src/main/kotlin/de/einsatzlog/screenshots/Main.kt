package de.einsatzlog.screenshots

import de.einsatzlog.app.ui.theme.EinsatzlogTheme
import java.io.File
import java.util.TimeZone

/**
 * Renders every store listing image: shot x locale x theme x device.
 *
 * Output goes straight into the layout `fastlane supply` / `deliver` expect, so
 * uploading is a path, not a copy step:
 *
 *   build/store/play/<locale>/images/<formFactor>/NN_name.png
 *   build/store/appstore/<locale>/NN_name_<device>.png
 *
 * Light theme is the listing set. Dark renders alongside it under a `dark/`
 * prefix so the "made for night operations" caption has an asset to use
 * without a second run.
 */
fun main() {
    // The app formats timestamps in the device timezone, so an unpinned JVM
    // default would make a CI runner (UTC) emit different clock times than a
    // laptop in Berlin. Pin it, or the images stop being reproducible.
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"))

    val outputRoot = File("build/store")
    outputRoot.deleteRecursively()

    var written = 0
    for (device in ALL_DEVICES) {
        for (locale in ALL_LOCALES) {
            for (dark in listOf(false, true)) {
                for (shot in ALL_SHOTS) {
                    if (shot.needsSupport && !device.store.supportEnabled) continue
                    val target = outputPath(outputRoot, device, locale, dark, shot)
                    renderToFile(device, locale, dark, target) {
                        EinsatzlogTheme(darkTheme = dark) { shot.content(device.store.supportEnabled) }
                    }
                    written++
                }
            }
        }
        println("  ${device.id.padEnd(16)} ${device.widthPx}x${device.heightPx} @${device.density}x")
    }

    println("\n$written images in ${outputRoot.absolutePath}")
    println("Play:      fastlane supply --metadata_path build/store/play")
    println("App Store: fastlane deliver --screenshots_path build/store/appstore")
}

private fun outputPath(
    root: File,
    device: DeviceSpec,
    locale: LocaleSpec,
    dark: Boolean,
    shot: Shot,
): File {
    val fileName = "%02d_%s.png".format(shot.order, shot.name)
    val themeDir = if (dark) "dark" else "."
    return when (device.store) {
        Store.PLAY -> File(
            root,
            "play/${locale.storeCode}/images/$themeDir/${device.playFolder}/$fileName",
        )
        Store.APP_STORE -> File(
            root,
            "appstore/${locale.storeCode}/$themeDir/${device.id}/$fileName",
        )
    }.normalizedPath()
}

/** Collapses the `.` used for the light (default) theme out of the path. */
private fun File.normalizedPath(): File = File(path.replace("/./", "/"))
