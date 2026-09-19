package de.einsatzlog.app.ui.components.preview

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import de.einsatzlog.app.domain.MessageType
import de.einsatzlog.app.support.SupportProduct
import de.einsatzlog.app.support.SupportTier
import de.einsatzlog.app.ui.entry.EntryFormContent
import de.einsatzlog.app.ui.entry.EntryFormState
import de.einsatzlog.app.ui.home.HomeContent
import de.einsatzlog.app.ui.home.HomeUiState
import de.einsatzlog.app.ui.logbook.LogbookContent
import de.einsatzlog.app.ui.settings.ImprintScreen
import de.einsatzlog.app.ui.settings.PrivacyScreen
import de.einsatzlog.app.ui.settings.SettingsScreen
import de.einsatzlog.app.ui.support.SupportContent
import de.einsatzlog.app.ui.support.SupportUiState
import de.einsatzlog.app.ui.theme.EinsatzlogTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/** Full-size themed wrapper for whole-screen previews (no padding box). */
@Composable
private fun ScreenFrame(dark: Boolean, content: @Composable () -> Unit) =
    EinsatzlogTheme(darkTheme = dark, content = content)

// --- Home ---

private val previewHomeState = HomeUiState(
    einsaetze = listOf(previewEinsatzActive, previewEinsatzClosed),
    entryCounts = mapOf("e1" to 9, "e2" to 3),
    query = "",
    loaded = true,
    isEmpty = false,
)

@Preview
@Composable
private fun HomeContentPreviewLight() = ScreenFrame(dark = false) {
    HomeContent(
        state = previewHomeState,
        snackbarHostState = remember { SnackbarHostState() },
        onQueryChange = {}, onOpenEinsatz = {}, onDeleteEinsatz = {},
        onCreateEinsatz = { _, _ -> }, onSeedDemo = {}, onOpenSupport = {}, onOpenSettings = {},
    )
}

@Preview
@Composable
private fun HomeContentPreviewDark() = ScreenFrame(dark = true) {
    HomeContent(
        state = previewHomeState,
        snackbarHostState = remember { SnackbarHostState() },
        onQueryChange = {}, onOpenEinsatz = {}, onDeleteEinsatz = {},
        onCreateEinsatz = { _, _ -> }, onSeedDemo = {}, onOpenSupport = {}, onOpenSettings = {},
    )
}

@Preview
@Composable
private fun HomeContentEmptyPreviewLight() = ScreenFrame(dark = false) {
    HomeContent(
        state = HomeUiState(loaded = true, isEmpty = true),
        snackbarHostState = remember { SnackbarHostState() },
        onQueryChange = {}, onOpenEinsatz = {}, onDeleteEinsatz = {},
        onCreateEinsatz = { _, _ -> }, onSeedDemo = {}, onOpenSupport = {}, onOpenSettings = {},
    )
}

// --- Logbook ---

private val previewLogbookEntries = listOf(
    previewEntry(MessageType.FUNKSPRUCH, "Florian 1 am Einsatzort eingetroffen"),
    previewEntry(MessageType.LAGEMELDUNG, "Zimmerbrand im EG, starke Rauchentwicklung"),
    previewEntry(MessageType.AUFTRAG, "Innenangriff über Haupteingang", source = "Einsatzleiter", target = "Trupp 1"),
)

@Preview
@Composable
private fun LogbookContentActivePreviewLight() = ScreenFrame(dark = false) {
    LogbookContent(
        einsatz = previewEinsatzActive, entries = previewLogbookEntries, isClosed = false, loaded = true,
        exporting = false, onBack = {}, onExport = {}, onToggleClose = {}, onNewEntry = {},
    )
}

@Preview
@Composable
private fun LogbookContentClosedPreviewDark() = ScreenFrame(dark = true) {
    LogbookContent(
        einsatz = previewEinsatzClosed, entries = previewLogbookEntries, isClosed = true, loaded = true,
        exporting = false, onBack = {}, onExport = {}, onToggleClose = {}, onNewEntry = {},
    )
}

// --- Quick-entry ---

private val previewEntryFormState = EntryFormState(
    timestampEpochMs = 1_752_998_700_000,
    type = MessageType.FUNKSPRUCH,
    source = "ELW",
    target = "Leitstelle",
    message = "Erkundung läuft",
    sourceSuggestions = listOf("ELW", "Florian 1"),
    targetSuggestions = listOf("Leitstelle", "Trupp 1"),
)

@Preview
@Composable
private fun EntryFormContentPreviewLight() = ScreenFrame(dark = false) {
    EntryFormContent(
        state = previewEntryFormState,
        onType = {}, onSource = {}, onTarget = {}, onMessage = {}, onBackdate = {}, onResetTime = {},
        onSubmit = {}, onBack = {},
    )
}

@Preview
@Composable
private fun EntryFormContentPreviewDark() = ScreenFrame(dark = true) {
    EntryFormContent(
        state = previewEntryFormState,
        onType = {}, onSource = {}, onTarget = {}, onMessage = {}, onBackdate = {}, onResetTime = {},
        onSubmit = {}, onBack = {},
    )
}

// --- Support ---

private val previewSupportProducts = listOf(
    SupportProduct(SupportTier.SMALL, "etb_support_small", "Kleine Spende", "1,99 €"),
    SupportProduct(SupportTier.MEDIUM, "etb_support_medium", "Mittlere Spende", "4,99 €"),
    SupportProduct(SupportTier.LARGE, "etb_support_large", "Große Spende", "9,99 €"),
    SupportProduct(SupportTier.SUBSCRIPTION, "etb_supporter", "Unterstützer-Abo", "0,99 €"),
)

@Preview
@Composable
private fun SupportContentPreviewLight() = ScreenFrame(dark = false) {
    SupportContent(
        state = SupportUiState.Loaded(products = previewSupportProducts, isSupporter = false),
        onPurchase = {}, onRestore = {}, onBack = {},
    )
}

@Preview
@Composable
private fun SupportContentSupporterPreviewLight() = ScreenFrame(dark = false) {
    SupportContent(
        state = SupportUiState.Loaded(products = previewSupportProducts, isSupporter = true),
        onPurchase = {}, onRestore = {}, onBack = {},
    )
}

@Preview
@Composable
private fun SupportContentUnavailablePreviewDark() = ScreenFrame(dark = true) {
    SupportContent(state = SupportUiState.Unavailable, onPurchase = {}, onRestore = {}, onBack = {})
}

// --- Settings & legal (no ViewModel — previewed directly) ---

@Preview
@Composable
private fun SettingsScreenPreviewLight() = ScreenFrame(dark = false) {
    SettingsScreen(versionName = PREVIEW_VERSION, onBack = {}, onOpenSupport = {}, onOpenPrivacy = {}, onOpenImprint = {}, onOpenLicenses = {})
}

@Preview
@Composable
private fun SettingsScreenPreviewDark() = ScreenFrame(dark = true) {
    SettingsScreen(versionName = PREVIEW_VERSION, onBack = {}, onOpenSupport = {}, onOpenPrivacy = {}, onOpenImprint = {}, onOpenLicenses = {})
}

@Preview
@Composable
private fun PrivacyScreenPreviewLight() = ScreenFrame(dark = false) { PrivacyScreen(onBack = {}) }

@Preview
@Composable
private fun ImprintScreenPreviewLight() = ScreenFrame(dark = false) { ImprintScreen(onBack = {}) }
