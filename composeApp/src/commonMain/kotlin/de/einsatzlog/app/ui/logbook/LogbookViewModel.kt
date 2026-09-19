package de.einsatzlog.app.ui.logbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.einsatzlog.app.core.export.LogbookExporter
import de.einsatzlog.app.core.export.buildPdfDocument
import de.einsatzlog.app.data.EinsatzRepository
import de.einsatzlog.app.data.db.EinsatzEntity
import de.einsatzlog.app.data.db.LogEntryEntity
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.OptIn

@OptIn(ExperimentalTime::class)

data class LogbookUiState(
    val einsatz: EinsatzEntity? = null,
    val entries: List<LogEntryEntity> = emptyList(),
    val loaded: Boolean = false,
) {
    val isClosed: Boolean get() = einsatz?.endedAtEpochMs != null
}

class LogbookViewModel(
    private val einsatzId: String,
    private val repository: EinsatzRepository,
    private val exporter: LogbookExporter,
    private val nowEpochMs: () -> Long = { Clock.System.now().toEpochMilliseconds() },
) : ViewModel() {

    private val _exporting = MutableStateFlow(false)
    val exporting: StateFlow<Boolean> = _exporting

    val uiState: StateFlow<LogbookUiState> = combine(
        repository.observeEinsatz(einsatzId),
        repository.observeEntries(einsatzId),
    ) { einsatz, entries ->
        LogbookUiState(einsatz = einsatz, entries = entries, loaded = true)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LogbookUiState())

    fun closeEinsatz() {
        viewModelScope.launch { repository.closeEinsatz(einsatzId) }
    }

    fun reopenEinsatz() {
        viewModelScope.launch { repository.reopenEinsatz(einsatzId) }
    }

    /** Export works on open AND closed Einsätze (spec 005, interim reports). */
    fun exportPdf() {
        val state = uiState.value
        val einsatz = state.einsatz ?: return
        if (_exporting.value) return
        _exporting.value = true
        viewModelScope.launch {
            exporter.exportPdf(
                buildPdfDocument(einsatz, state.entries, generatedAtEpochMs = nowEpochMs())
            )
            _exporting.value = false
        }
    }
}
