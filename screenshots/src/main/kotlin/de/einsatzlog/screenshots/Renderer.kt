package de.einsatzlog.screenshots

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SkikoComposeUiTest
import androidx.compose.ui.unit.Density
import java.awt.image.BufferedImage
import java.io.File
import java.util.Locale
import javax.imageio.ImageIO

/**
 * Renders a composable to an off-screen Skia surface of exactly
 * [DeviceSpec.widthPx] x [DeviceSpec.heightPx] and writes a store-compliant PNG.
 *
 * Uses the Compose test harness rather than a raw `ImageComposeScene` on
 * purpose: string resources load asynchronously, so a single rendered frame can
 * catch the UI mid-load with empty labels. `waitForIdle()` settles composition
 * and its effects first, which makes the output deterministic.
 */
@OptIn(ExperimentalTestApi::class)
fun renderToFile(
    spec: DeviceSpec,
    locale: LocaleSpec,
    darkTheme: Boolean,
    target: File,
    content: @Composable () -> Unit,
) {
    // Compose resources resolve against the JVM default locale on desktop, so
    // this is what picks values/ (de) vs values-en/.
    Locale.setDefault(Locale.forLanguageTag(locale.javaTag))

    var awt: BufferedImage? = null
    SkikoComposeUiTest(
        width = spec.widthPx,
        height = spec.heightPx,
        density = Density(spec.density),
    ).runTest {
        setContent(content)
        waitForIdle()
        awt = captureToImage().toAwtImage()
    }

    val captured = checkNotNull(awt) { "capture produced no image for ${target.name}" }
    target.parentFile.mkdirs()
    ImageIO.write(captured.flattenedForStores(darkTheme), "png", target)
}

/**
 * Both stores reject images with an alpha channel, and the capture surface has
 * one. Composite onto an opaque backdrop matching the theme so nothing shows a
 * white halo in dark mode, and hand ImageIO a 24-bit RGB raster.
 */
private fun BufferedImage.flattenedForStores(darkTheme: Boolean): BufferedImage {
    val out = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val g = out.createGraphics()
    g.color = if (darkTheme) java.awt.Color(0x11, 0x14, 0x18) else java.awt.Color.WHITE
    g.fillRect(0, 0, width, height)
    g.drawImage(this, 0, 0, null)
    g.dispose()
    return out
}
