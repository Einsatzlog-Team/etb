package de.einsatzlog.screenshots

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import de.einsatzlog.app.domain.MessageType
import de.einsatzlog.app.support.SupportProduct
import de.einsatzlog.app.support.SupportTier
import de.einsatzlog.app.ui.components.preview.PREVIEW_VERSION
import de.einsatzlog.app.ui.entry.EntryFormContent
import de.einsatzlog.app.ui.entry.EntryFormState
import de.einsatzlog.app.ui.home.HomeContent
import de.einsatzlog.app.ui.home.HomeUiState
import de.einsatzlog.app.ui.logbook.LogbookContent
import de.einsatzlog.app.ui.settings.SettingsScreen
import de.einsatzlog.app.ui.support.SupportContent
import de.einsatzlog.app.ui.support.SupportUiState

/**
 * One store image: a stable file name and the screen to draw. [content] gets
 * `supportEnabled` — false for stores whose release ships without in-app
 * purchases (the first iOS release), so no heart or support link is shown.
 * [needsSupport] shots are skipped entirely for those stores.
 */
class Shot(
    val order: Int,
    val name: String,
    val needsSupport: Boolean = false,
    val content: @Composable (supportEnabled: Boolean) -> Unit,
)

// --- Screen states --------------------------------------------------------
// Data lives in DemoData.kt; these just wrap it in the state each screen takes.

private val homeState = HomeUiState(
    einsaetze = demoEinsaetze,
    entryCounts = demoEntryCounts,
    query = "",
    loaded = true,
    isEmpty = false,
)

private val entryFormState = EntryFormState(
    // 19:42 + 74 min — the next entry, mid-typing, with the heuristic
    // provider's suggestions already narrowed to this Einsatz's call signs.
    timestampEpochMs = demoEntries.last().timestampEpochMs + 4 * 60_000L,
    type = MessageType.LAGEMELDUNG,
    source = "ELW 1",
    target = "Leitstelle",
    message = "Einsatz beendet, Rückfahrt angetreten",
    sourceSuggestions = listOf("ELW 1", "HLF 20", "DLK 23", "Angriffstrupp", "EL"),
    targetSuggestions = listOf("Leitstelle", "EL", "Wassertrupp", "RTW"),
)

// Mirrors what ships: one tier (etb_support_small) — store images must match the app.
private val supportState = SupportUiState.Loaded(
    products = listOf(
        SupportProduct(SupportTier.SMALL, "etb_support_small", "Kleine Unterstützung", "1,99\u00a0\u20ac"),
    ),
    isSupporter = false,
)

// --- The listing set --------------------------------------------------------
// Ordered to match the caption list in the marketing copy: stores show them in
// upload order, so the file prefix is the running order.

val ALL_SHOTS: List<Shot> = listOf(
    Shot(1, "home") { supportEnabled ->
        HomeContent(
            state = homeState,
            snackbarHostState = SnackbarHostState(),
            onQueryChange = {}, onOpenEinsatz = {}, onDeleteEinsatz = {},
            onCreateEinsatz = { _, _ -> }, onSeedDemo = {},
            onOpenSupport = if (supportEnabled) ({}) else null, onOpenSettings = {},
        )
    },
    Shot(2, "quick-entry") { _ ->
        EntryFormContent(
            state = entryFormState,
            onType = {}, onSource = {}, onTarget = {}, onMessage = {},
            onBackdate = {}, onResetTime = {}, onSubmit = {}, onBack = {},
        )
    },
    Shot(3, "logbook") { _ ->
        LogbookContent(
            einsatz = demoZimmerbrand,
            entries = demoEntries,
            isClosed = false,
            loaded = true,
            exporting = false,
            onBack = {}, onExport = {}, onToggleClose = {}, onNewEntry = {},
        )
    },
    Shot(4, "support", needsSupport = true) { _ ->
        SupportContent(state = supportState, onPurchase = {}, onRestore = {}, onBack = {})
    },
    Shot(5, "settings") { supportEnabled ->
        SettingsScreen(
            versionName = PREVIEW_VERSION,
            onBack = {}, onOpenSupport = if (supportEnabled) ({}) else null, onOpenPrivacy = {},
            onOpenImprint = {}, onOpenLicenses = {},
        )
    },
)
