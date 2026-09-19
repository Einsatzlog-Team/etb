package de.einsatzlog.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.einsatzlog.app.core.text.matchesSearch
import de.einsatzlog.app.data.EinsatzRepository
import de.einsatzlog.app.data.db.EinsatzEntity
import de.einsatzlog.app.data.demo.seedDemoEinsatz
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: EinsatzRepository,
    private val undoWindowMs: Long = 12_000, // outlives the 10s Long snackbar (spec 002: ≥5s undo)
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val pendingDeleteId = MutableStateFlow<String?>(null)
    private var pendingDeleteJob: Job? = null

    val uiState: StateFlow<HomeUiState> = combine(
        repository.observeEinsaetze(),
        repository.observeEntryCounts(),
        query,
        pendingDeleteId,
    ) { einsaetze, counts, q, pendingDelete ->
        val visible = einsaetze
            .filterNot { it.id == pendingDelete }
            .filter { q.isBlank() || matchesSearch(it.name, q) }
        HomeUiState(
            einsaetze = visible,
            entryCounts = counts,
            query = q,
            loaded = true,
            isEmpty = einsaetze.none { it.id != pendingDelete },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun createEinsatz(name: String, description: String?) {
        if (name.isBlank()) return
        viewModelScope.launch { repository.createEinsatz(name, description) }
    }

    /** Hide immediately, delete after the undo window (spec 002: undo over confirm dialogs). */
    fun requestDelete(einsatzId: String) {
        pendingDeleteJob?.cancel()
        pendingDeleteId.value = einsatzId
        pendingDeleteJob = viewModelScope.launch {
            delay(undoWindowMs)
            commitPendingDelete()
        }
    }

    fun undoDelete() {
        pendingDeleteJob?.cancel()
        pendingDeleteJob = null
        pendingDeleteId.value = null
    }

    fun commitPendingDeleteNow() {
        pendingDeleteJob?.cancel()
        pendingDeleteJob = null
        viewModelScope.launch { commitPendingDelete() }
    }

    private suspend fun commitPendingDelete() {
        pendingDeleteId.value?.let { repository.deleteEinsatz(it) }
        pendingDeleteId.value = null
    }

    fun seedDemo(demoName: String) {
        viewModelScope.launch { seedDemoEinsatz(repository, demoName) }
    }
}
