package de.einsatzlog.app.ui.components.preview

import androidx.compose.runtime.Composable
import de.einsatzlog.app.domain.MessageType
import de.einsatzlog.app.ui.components.organisms.EinsatzHeader
import de.einsatzlog.app.ui.components.organisms.LogEntryStream
import de.einsatzlog.app.ui.components.organisms.QuickEntryForm
import org.jetbrains.compose.ui.tooling.preview.Preview

private const val ORGANISM_WIDTH = 360

// --- EinsatzHeader ---

@Preview
@Composable
private fun EinsatzHeaderActivePreviewLight() = PreviewSurface(dark = false, width = ORGANISM_WIDTH) {
    EinsatzHeader(einsatz = previewEinsatzActive, isClosed = false)
}

@Preview
@Composable
private fun EinsatzHeaderClosedPreviewLight() = PreviewSurface(dark = false, width = ORGANISM_WIDTH) {
    EinsatzHeader(einsatz = previewEinsatzClosed, isClosed = true)
}

@Preview
@Composable
private fun EinsatzHeaderActivePreviewDark() = PreviewSurface(dark = true, width = ORGANISM_WIDTH) {
    EinsatzHeader(einsatz = previewEinsatzActive, isClosed = false)
}

// --- LogEntryStream ---

private val previewStream = listOf(
    previewEntry(MessageType.FUNKSPRUCH, "Florian 1 am Einsatzort eingetroffen"),
    previewEntry(MessageType.LAGEMELDUNG, "Zimmerbrand im EG, starke Rauchentwicklung"),
    previewEntry(MessageType.AUFTRAG, "Innenangriff über Haupteingang", source = "Einsatzleiter", target = "Trupp 1"),
    previewEntry(MessageType.ANFORDERUNG, "Benötigen Rettungsdienst", target = "Leitstelle"),
    previewEntry(MessageType.DOKUMENTATION, "Brand unter Kontrolle", source = null, target = null),
)

@Preview
@Composable
private fun LogEntryStreamPreviewLight() = PreviewSurface(dark = false, width = ORGANISM_WIDTH) {
    LogEntryStream(entries = previewStream, loaded = true)
}

@Preview
@Composable
private fun LogEntryStreamPreviewDark() = PreviewSurface(dark = true, width = ORGANISM_WIDTH) {
    LogEntryStream(entries = previewStream, loaded = true)
}

@Preview
@Composable
private fun LogEntryStreamEmptyPreviewLight() = PreviewSurface(dark = false, width = ORGANISM_WIDTH) {
    LogEntryStream(entries = emptyList(), loaded = true)
}

// --- QuickEntryForm ---

@Preview
@Composable
private fun QuickEntryFormPreviewLight() = PreviewSurface(dark = false, width = ORGANISM_WIDTH) {
    QuickEntryForm(
        timestampEpochMs = 1_752_998_700_000,
        type = MessageType.FUNKSPRUCH,
        source = "ELW",
        target = "",
        message = "",
        sourceSuggestions = listOf("ELW", "Florian 1", "Leitstelle"),
        targetSuggestions = listOf("Leitstelle", "Trupp 1"),
        onType = {}, onSource = {}, onTarget = {}, onMessage = {}, onBackdate = {}, onResetTime = {},
    )
}

@Preview
@Composable
private fun QuickEntryFormPreviewDark() = PreviewSurface(dark = true, width = ORGANISM_WIDTH) {
    QuickEntryForm(
        timestampEpochMs = 1_752_998_700_000,
        type = MessageType.LAGEMELDUNG,
        source = "Florian 1",
        target = "Leitstelle",
        message = "Feuer unter Kontrolle",
        sourceSuggestions = listOf("ELW", "Florian 1"),
        targetSuggestions = listOf("Leitstelle"),
        onType = {}, onSource = {}, onTarget = {}, onMessage = {}, onBackdate = {}, onResetTime = {},
    )
}
