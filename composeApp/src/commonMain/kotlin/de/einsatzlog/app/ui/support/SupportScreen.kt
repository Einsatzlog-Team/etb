package de.einsatzlog.app.ui.support

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

/** ViewModel wrapper — delegates to the stateless [SupportContent]. */
@Composable
fun SupportScreen(
    onBack: () -> Unit,
    viewModel: SupportViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    SupportContent(
        state = state,
        onPurchase = viewModel::purchase,
        onRestore = viewModel::restore,
        onBack = onBack,
    )
}
