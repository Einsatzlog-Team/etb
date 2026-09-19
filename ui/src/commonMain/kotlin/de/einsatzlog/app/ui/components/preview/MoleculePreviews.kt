package de.einsatzlog.app.ui.components.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import de.einsatzlog.app.domain.MessageType
import de.einsatzlog.app.ui.components.molecules.EinsatzListItem
import de.einsatzlog.app.ui.components.molecules.EntryTypeSelector
import de.einsatzlog.app.ui.components.molecules.LogEntryCard
import de.einsatzlog.app.ui.components.molecules.SuggestionChipRow
import de.einsatzlog.app.ui.components.molecules.SupportTierCard
import org.jetbrains.compose.ui.tooling.preview.Preview

private const val PREVIEW_WIDTH = 360

// --- EinsatzListItem: active + closed together ---

@Preview
@Composable
private fun EinsatzListItemPreviewLight() = PreviewSurface(dark = false, width = PREVIEW_WIDTH) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        EinsatzListItem(previewEinsatzActive, entryCount = 9, onClick = {}, onLongClick = {})
        EinsatzListItem(previewEinsatzClosed, entryCount = 3, onClick = {}, onLongClick = {})
    }
}

@Preview
@Composable
private fun EinsatzListItemPreviewDark() = PreviewSurface(dark = true, width = PREVIEW_WIDTH) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        EinsatzListItem(previewEinsatzActive, entryCount = 9, onClick = {}, onLongClick = {})
        EinsatzListItem(previewEinsatzClosed, entryCount = 3, onClick = {}, onLongClick = {})
    }
}

// --- EntryTypeSelector ---

@Preview
@Composable
private fun EntryTypeSelectorPreviewLight() = PreviewSurface(dark = false, width = PREVIEW_WIDTH) {
    EntryTypeSelector(selected = MessageType.FUNKSPRUCH, onSelect = {})
}

@Preview
@Composable
private fun EntryTypeSelectorPreviewDark() = PreviewSurface(dark = true, width = PREVIEW_WIDTH) {
    EntryTypeSelector(selected = MessageType.LAGEMELDUNG, onSelect = {})
}

// --- LogEntryCard: one per message type, showing the color coding in a stream ---

@Preview
@Composable
private fun LogEntryCardPreviewLight() = PreviewSurface(dark = false, width = PREVIEW_WIDTH) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        LogEntryCard(previewEntry(MessageType.FUNKSPRUCH, "Florian 1 am Einsatzort eingetroffen"))
        LogEntryCard(previewEntry(MessageType.LAGEMELDUNG, "Zimmerbrand im EG, starke Rauchentwicklung"))
        LogEntryCard(previewEntry(MessageType.AUFTRAG, "Innenangriff über Haupteingang", source = "Einsatzleiter", target = "Trupp 1"))
        LogEntryCard(previewEntry(MessageType.DOKUMENTATION, "Brand unter Kontrolle", source = null, target = null))
    }
}

@Preview
@Composable
private fun LogEntryCardPreviewDark() = PreviewSurface(dark = true, width = PREVIEW_WIDTH) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        LogEntryCard(previewEntry(MessageType.FUNKSPRUCH, "Florian 1 am Einsatzort eingetroffen"))
        LogEntryCard(previewEntry(MessageType.ANFORDERUNG, "Benötigen Rettungsdienst", target = "Leitstelle"))
    }
}

// --- SuggestionChipRow ---

@Preview
@Composable
private fun SuggestionChipRowPreviewLight() = PreviewSurface(dark = false, width = PREVIEW_WIDTH) {
    SuggestionChipRow(suggestions = listOf("ELW", "Florian 1", "Leitstelle", "Trupp 1"), onPick = {})
}

@Preview
@Composable
private fun SuggestionChipRowPreviewDark() = PreviewSurface(dark = true, width = PREVIEW_WIDTH) {
    SuggestionChipRow(suggestions = listOf("ELW", "Florian 1", "Leitstelle"), onPick = {})
}

// --- SupportTierCard: a donation tier and the subscription ---

@Preview
@Composable
private fun SupportTierCardPreviewLight() = PreviewSurface(dark = false, width = PREVIEW_WIDTH) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SupportTierCard(title = "Kleine Spende", subtitle = null, priceLabel = "1,99 €", busy = false, onClick = {})
        SupportTierCard(
            title = "Unterstützer-Abo",
            subtitle = "Monatlich, jederzeit kündbar – 7 Tage kostenlos testen",
            priceLabel = "0,99 €",
            busy = false,
            onClick = {},
        )
        SupportTierCard(title = "Mittlere Spende", subtitle = null, priceLabel = "4,99 €", busy = true, onClick = {})
    }
}

@Preview
@Composable
private fun SupportTierCardPreviewDark() = PreviewSurface(dark = true, width = PREVIEW_WIDTH) {
    SupportTierCard(title = "Große Spende", subtitle = null, priceLabel = "9,99 €", busy = false, onClick = {})
}
