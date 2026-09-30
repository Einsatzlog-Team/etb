package de.einsatzlog.app.ui.logbook

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.einsatzlog.app.ui.components.templates.LayoutClass
import de.einsatzlog.app.ui.components.templates.StreamWithEntryPaneTemplate
import de.einsatzlog.app.ui.entry.EntryFormScreen
import de.einsatzlog.app.ui.theme.Spacing
import einsatzlog.ui.generated.resources.Res
import einsatzlog.ui.generated.resources.logbook_new_entry
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * The logbook as the detail pane of the tablet layout. Medium width: the
 * logbook alone, "New entry" opens the full-screen form. Expanded width: a
 * permanent entry pane beside the stream.
 *
 * The pane keeps the phone's timestamp rule: the time is captured when the
 * form opens (a fresh ViewModel per opened form), not when the pane appears.
 */
@Composable
fun LogbookPane(
    einsatzId: String,
    layout: LayoutClass,
    onClose: () -> Unit,
    onNewEntryFullScreen: () -> Unit,
) {
    // Keyed per incident: the pane lives in the Home destination's ViewModelStore,
    // so without a key picking another incident would reuse the old ViewModel.
    val viewModel: LogbookViewModel =
        koinViewModel(key = "logbook-$einsatzId", parameters = { parametersOf(einsatzId) })

    if (layout != LayoutClass.EXPANDED) {
        LogbookScreen(einsatzId = einsatzId, onBack = onClose, onNewEntry = onNewEntryFullScreen, viewModel = viewModel)
        return
    }

    var entryOpen by rememberSaveable(einsatzId) { mutableStateOf(false) }
    var entrySession by rememberSaveable(einsatzId) { mutableIntStateOf(0) }
    val openEntry = { entrySession++; entryOpen = true }
    val state by viewModel.uiState.collectAsState()

    StreamWithEntryPaneTemplate(
        stream = {
            // No floating "New entry" here – the pane beside it is the entry point.
            LogbookScreen(einsatzId = einsatzId, onBack = onClose, onNewEntry = null, viewModel = viewModel)
        },
        pane = {
            if (entryOpen && !state.isClosed) {
                key(entrySession) {
                    EntryFormScreen(
                        einsatzId = einsatzId,
                        onDone = { entryOpen = false },
                        viewModel = koinViewModel(
                            key = "entry-$einsatzId-$entrySession",
                            parameters = { parametersOf(einsatzId) },
                        ),
                    )
                }
            } else {
                Box(Modifier.fillMaxSize().padding(Spacing.l), contentAlignment = Alignment.Center) {
                    Button(
                        onClick = openEntry,
                        enabled = state.loaded && !state.isClosed,
                        modifier = Modifier.fillMaxWidth().heightIn(min = Spacing.touchTarget * 1.5f),
                    ) {
                        Text(stringResource(Res.string.logbook_new_entry))
                    }
                }
            }
        },
    )
}
