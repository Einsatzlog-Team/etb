package de.einsatzlog.app.ui.logbook

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/** ViewModel wrapper — collects state, delegates to the stateless [LogbookContent]. */
@Composable
fun LogbookScreen(
    einsatzId: String,
    onBack: () -> Unit,
    onNewEntry: () -> Unit,
    viewModel: LogbookViewModel = koinViewModel(parameters = { parametersOf(einsatzId) }),
) {
    val state by viewModel.uiState.collectAsState()
    val exporting by viewModel.exporting.collectAsState()
    LogbookContent(
        einsatz = state.einsatz,
        entries = state.entries,
        isClosed = state.isClosed,
        loaded = state.loaded,
        exporting = exporting,
        onBack = onBack,
        onExport = viewModel::exportPdf,
        onToggleClose = { if (state.isClosed) viewModel.reopenEinsatz() else viewModel.closeEinsatz() },
        onNewEntry = onNewEntry,
    )
}
