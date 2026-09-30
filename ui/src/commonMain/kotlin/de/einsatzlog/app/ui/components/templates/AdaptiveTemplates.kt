package de.einsatzlog.app.ui.components.templates

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import de.einsatzlog.app.ui.theme.Spacing

/**
 * Template level (design/design-system.md): layout scaffolding per window
 * size, no content of its own. Phones never see these – they keep the
 * one-screen-at-a-time navigation.
 */
enum class LayoutClass { COMPACT, MEDIUM, EXPANDED }

/** Material 3 width breakpoints: < 600 dp compact, < 840 dp medium, else expanded. */
fun layoutClassForWidth(widthDp: Float): LayoutClass = when {
    widthDp >= WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND -> LayoutClass.EXPANDED
    widthDp >= WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND -> LayoutClass.MEDIUM
    else -> LayoutClass.COMPACT
}

@Composable
fun currentLayoutClass(): LayoutClass {
    val size = currentWindowAdaptiveInfo().windowSizeClass
    return when {
        size.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> LayoutClass.EXPANDED
        size.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> LayoutClass.MEDIUM
        else -> LayoutClass.COMPACT
    }
}

/** Incident list on the left, the selected incident on the right. */
@Composable
fun ListDetailTemplate(
    list: @Composable () -> Unit,
    detail: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    listWidth: Dp = 360.dp,
) {
    Surface(modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) { Row(Modifier.fillMaxSize()) {
        Box(Modifier.width(listWidth).fillMaxHeight()) { list() }
        VerticalDivider()
        Box(Modifier.weight(1f).fillMaxHeight()) { detail() }
    } }
}

/** The logbook stream with a permanent entry pane beside it (expanded width). */
@Composable
fun StreamWithEntryPaneTemplate(
    stream: @Composable () -> Unit,
    pane: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    paneWidth: Dp = 380.dp,
) {
    Surface(modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) { Row(Modifier.fillMaxSize()) {
        Box(Modifier.weight(1f).fillMaxHeight()) { stream() }
        VerticalDivider()
        Box(Modifier.width(paneWidth).fillMaxHeight()) { pane() }
    } }
}

/** Calm placeholder for the detail pane before anything is selected. */
@Composable
fun EmptyDetail(text: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().padding(Spacing.xl), contentAlignment = Alignment.Center) {
        Text(
            text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
