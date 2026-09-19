package de.einsatzlog.app.ui.components.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import de.einsatzlog.app.domain.MessageType
import de.einsatzlog.app.ui.components.atoms.ActiveBadge
import de.einsatzlog.app.ui.components.atoms.SupporterBadge
import de.einsatzlog.app.ui.components.atoms.TypeChip
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun ActiveBadgePreviewLight() = PreviewSurface(dark = false) { ActiveBadge() }

@Preview
@Composable
private fun ActiveBadgePreviewDark() = PreviewSurface(dark = true) { ActiveBadge() }

@Preview
@Composable
private fun SupporterBadgePreviewLight() = PreviewSurface(dark = false) { SupporterBadge() }

@Preview
@Composable
private fun SupporterBadgePreviewDark() = PreviewSurface(dark = true) { SupporterBadge() }

/** All five message-type chips together — the core color coding, light + dark. */
@Preview
@Composable
private fun TypeChipsPreviewLight() = PreviewSurface(dark = false) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MessageType.entries.forEach { TypeChip(it) }
    }
}

@Preview
@Composable
private fun TypeChipsPreviewDark() = PreviewSurface(dark = true) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MessageType.entries.forEach { TypeChip(it) }
    }
}
