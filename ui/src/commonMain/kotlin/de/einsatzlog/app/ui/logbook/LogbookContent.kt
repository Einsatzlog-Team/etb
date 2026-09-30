package de.einsatzlog.app.ui.logbook

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.einsatzlog.app.data.db.EinsatzEntity
import de.einsatzlog.app.data.db.LogEntryEntity
import de.einsatzlog.app.ui.components.organisms.EinsatzHeader
import de.einsatzlog.app.ui.components.organisms.LogEntryStream
import einsatzlog.ui.generated.resources.Res
import einsatzlog.ui.generated.resources.logbook_back
import einsatzlog.ui.generated.resources.logbook_close
import einsatzlog.ui.generated.resources.logbook_export
import einsatzlog.ui.generated.resources.logbook_new_entry
import einsatzlog.ui.generated.resources.logbook_reopen
import org.jetbrains.compose.resources.stringResource

/** Stateless logbook: header + read-only stream + lifecycle actions (spec 002/003, sketch 02). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogbookContent(
    einsatz: EinsatzEntity?,
    entries: List<LogEntryEntity>,
    isClosed: Boolean,
    loaded: Boolean,
    exporting: Boolean,
    onBack: () -> Unit,
    onExport: () -> Unit,
    onToggleClose: () -> Unit,
    /** Null hides the button – the tablet layout has a permanent entry pane instead. */
    onNewEntry: (() -> Unit)?,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(einsatz?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.logbook_back),
                        )
                    }
                },
                actions = {
                    if (einsatz != null) {
                        IconButton(onClick = onExport, enabled = !exporting) {
                            Icon(Icons.Filled.Share, contentDescription = stringResource(Res.string.logbook_export))
                        }
                        TextButton(onClick = onToggleClose) {
                            Text(
                                stringResource(
                                    if (isClosed) Res.string.logbook_reopen else Res.string.logbook_close
                                )
                            )
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            if (einsatz != null && !isClosed && onNewEntry != null) {
                ExtendedFloatingActionButton(onClick = onNewEntry) {
                    Text(stringResource(Res.string.logbook_new_entry))
                }
            }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (einsatz != null) {
                EinsatzHeader(einsatz = einsatz, isClosed = isClosed)
            }
            LogEntryStream(entries = entries, loaded = loaded)
        }
    }
}
