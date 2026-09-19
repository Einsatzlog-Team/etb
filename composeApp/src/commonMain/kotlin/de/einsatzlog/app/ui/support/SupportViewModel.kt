package de.einsatzlog.app.ui.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.einsatzlog.app.support.PurchaseOutcome
import de.einsatzlog.app.support.SupportProduct
import de.einsatzlog.app.support.SupportRepository
import de.einsatzlog.app.support.SupportTier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SupportViewModel(
    private val repository: SupportRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<SupportUiState>(SupportUiState.Loading)
    val state: StateFlow<SupportUiState> = _state.asStateFlow()

    init {
        load()
        viewModelScope.launch {
            repository.supporterStatus.collect { status ->
                _state.update { s ->
                    if (s is SupportUiState.Loaded) s.copy(isSupporter = status.isSupporter) else s
                }
            }
        }
    }

    fun load() {
        if (!repository.isAvailable) {
            _state.value = SupportUiState.Unavailable
            return
        }
        _state.value = SupportUiState.Loading
        viewModelScope.launch {
            repository.loadProducts()
                .onSuccess { products ->
                    _state.value =
                        if (products.isEmpty()) SupportUiState.Unavailable
                        else SupportUiState.Loaded(products = products, isSupporter = false)
                }
                .onFailure { _state.value = SupportUiState.Unavailable }
        }
    }

    fun purchase(tier: SupportTier) {
        val current = _state.value as? SupportUiState.Loaded ?: return
        if (current.purchasing != null) return
        _state.value = current.copy(purchasing = tier, error = null, paymentPending = false)
        viewModelScope.launch {
            val outcome = repository.purchase(tier)
            _state.update { s ->
                if (s !is SupportUiState.Loaded) return@update s
                when (outcome) {
                    PurchaseOutcome.Success -> s.copy(purchasing = null)
                    PurchaseOutcome.Cancelled -> s.copy(purchasing = null) // silent (spec 004)
                    is PurchaseOutcome.Error ->
                        if (outcome.message.contains("ausstehend")) {
                            s.copy(purchasing = null, paymentPending = true)
                        } else {
                            s.copy(purchasing = null, error = outcome.message)
                        }
                }
            }
        }
    }

    fun restore() {
        viewModelScope.launch { repository.restorePurchases() }
    }
}
