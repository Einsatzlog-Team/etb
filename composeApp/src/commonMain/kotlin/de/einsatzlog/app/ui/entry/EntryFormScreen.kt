package de.einsatzlog.app.ui.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/** ViewModel wrapper — collects state, delegates to the stateless [EntryFormContent]. */
@Composable
fun EntryFormScreen(
    einsatzId: String,
    onDone: () -> Unit,
    viewModel: EntryFormViewModel = koinViewModel(parameters = { parametersOf(einsatzId) }),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.submitted.collect { onDone() }
    }

    EntryFormContent(
        state = state,
        onType = viewModel::onTypeChange,
        onSource = viewModel::onSourceChange,
        onTarget = viewModel::onTargetChange,
        onMessage = viewModel::onMessageChange,
        onBackdate = viewModel::backdate,
        onResetTime = viewModel::resetTimestamp,
        onSubmit = viewModel::submit,
        onBack = onDone,
    )
}
