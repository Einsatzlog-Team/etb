package de.einsatzlog.screenshots

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.einsatzlog.app.ui.components.templates.LayoutClass
import de.einsatzlog.app.ui.components.templates.ListDetailTemplate
import de.einsatzlog.app.ui.components.templates.StreamWithEntryPaneTemplate
import de.einsatzlog.app.ui.components.templates.layoutClassForWidth
import de.einsatzlog.app.ui.entry.EntryFormContent
import de.einsatzlog.app.ui.home.HomeContent
import de.einsatzlog.app.ui.home.HomeUiState
import de.einsatzlog.app.ui.logbook.LogbookContent
import de.einsatzlog.app.ui.theme.Spacing
import einsatzlog.ui.generated.resources.Res
import einsatzlog.ui.generated.resources.logbook_new_entry
import org.jetbrains.compose.resources.stringResource

// Tablet set: the same templates the app uses (feature/tablet-layout), filled
// with the demo data. The layout follows the canvas width exactly like the app
// does – landscape is three panes, portrait is list + logbook.

private val tabletHomeState = HomeUiState(
    einsaetze = demoEinsaetze,
    entryCounts = demoEntryCounts,
    query = "",
    loaded = true,
    isEmpty = false,
)

@Composable
private fun TabletLayout(entryOpen: Boolean, supportEnabled: Boolean) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val layout = layoutClassForWidth(maxWidth.value)
        if (layout != LayoutClass.EXPANDED && entryOpen) {
            // Medium width: "New entry" opens the full-screen form, as in the app.
            EntryForm()
            return@BoxWithConstraints
        }
        ListDetailTemplate(
            list = {
                HomeContent(
                    state = tabletHomeState,
                    snackbarHostState = SnackbarHostState(),
                    onQueryChange = {}, onOpenEinsatz = {}, onDeleteEinsatz = {},
                    onCreateEinsatz = { _, _ -> }, onSeedDemo = {},
                    onOpenSupport = if (supportEnabled) ({}) else null, onOpenSettings = {},
                    selectedEinsatzId = demoZimmerbrand.id,
                )
            },
            detail = {
                val expanded = layout == LayoutClass.EXPANDED
                val stream: @Composable () -> Unit = {
                    LogbookContent(
                        einsatz = demoZimmerbrand,
                        entries = demoEntries,
                        isClosed = false,
                        loaded = true,
                        exporting = false,
                        onBack = {}, onExport = {}, onToggleClose = {},
                        onNewEntry = if (expanded) null else ({}),
                    )
                }
                if (expanded) {
                    StreamWithEntryPaneTemplate(
                        stream = stream,
                        pane = { if (entryOpen) EntryForm() else IdlePane() },
                    )
                } else {
                    stream()
                }
            },
        )
    }
}

@Composable
private fun EntryForm() = EntryFormContent(
    state = entryFormState,
    onType = {}, onSource = {}, onTarget = {}, onMessage = {},
    onBackdate = {}, onResetTime = {}, onSubmit = {}, onBack = {},
)

@Composable
private fun IdlePane() {
    Box(Modifier.fillMaxSize().padding(Spacing.l), contentAlignment = Alignment.Center) {
        Button(onClick = {}, modifier = Modifier.fillMaxWidth().heightIn(min = Spacing.touchTarget * 1.5f)) {
            Text(stringResource(Res.string.logbook_new_entry))
        }
    }
}

val TABLET_SHOTS: List<Shot> = listOf(
    Shot(1, "tablet-logbook") { supportEnabled -> TabletLayout(entryOpen = false, supportEnabled = supportEnabled) },
    Shot(2, "tablet-quick-entry") { supportEnabled -> TabletLayout(entryOpen = true, supportEnabled = supportEnabled) },
)
