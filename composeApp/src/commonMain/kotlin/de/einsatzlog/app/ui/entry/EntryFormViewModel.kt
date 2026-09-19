package de.einsatzlog.app.ui.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.einsatzlog.app.data.EinsatzRepository
import de.einsatzlog.app.domain.MessageType
import de.einsatzlog.app.suggestions.SuggestionProvider
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalTime::class)
class EntryFormViewModel(
    private val einsatzId: String,
    private val repository: EinsatzRepository,
    private val suggestions: SuggestionProvider,
    private val nowEpochMs: () -> Long = { Clock.System.now().toEpochMilliseconds() },
) : ViewModel() {

    private val _state = MutableStateFlow(EntryFormState(timestampEpochMs = nowEpochMs()))
    val state: StateFlow<EntryFormState> = _state.asStateFlow()

    private val _submitted = MutableSharedFlow<Unit>()
    val submitted: SharedFlow<Unit> = _submitted.asSharedFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(sourceSuggestions = suggestions.suggestSources(einsatzId)) }
            refreshTargetSuggestions()
        }
    }

    fun onTypeChange(type: MessageType) = _state.update { it.copy(type = type) }

    fun onSourceChange(value: String) {
        _state.update { it.copy(source = value) }
        refreshTargetSuggestions()
    }

    fun onTargetChange(value: String) = _state.update { it.copy(target = value) }

    fun onMessageChange(value: String) = _state.update { it.copy(message = value) }

    /** Backdate chips (−1/−5 min). Never into the future: only subtraction exists. */
    fun backdate(minutes: Int) = _state.update {
        it.copy(timestampEpochMs = it.timestampEpochMs - minutes * 60_000L)
    }

    /** Reset the timestamp to the original "Neuer Eintrag" moment. */
    fun resetTimestamp() = _state.update { it.copy(timestampEpochMs = initialTimestamp) }

    private val initialTimestamp = _state.value.timestampEpochMs

    fun submit() {
        val s = _state.value
        if (!s.canSubmit) return
        _state.update { it.copy(submitting = true) }
        viewModelScope.launch {
            repository.addEntry(
                einsatzId = einsatzId,
                type = s.type,
                source = s.source.takeIf { it.isNotBlank() },
                target = s.target.takeIf { it.isNotBlank() },
                message = s.message,
                timestampEpochMs = s.timestampEpochMs,
            )
            _submitted.emit(Unit)
        }
    }

    private fun refreshTargetSuggestions() {
        viewModelScope.launch {
            val source = _state.value.source.takeIf { it.isNotBlank() }
            _state.update {
                it.copy(targetSuggestions = suggestions.suggestTargets(einsatzId, source))
            }
        }
    }
}
