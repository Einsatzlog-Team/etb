package de.einsatzlog.app.ui.home

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import einsatzlog.ui.generated.resources.Res
import einsatzlog.ui.generated.resources.home_delete_done
import einsatzlog.ui.generated.resources.home_delete_undo
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/** ViewModel wrapper — owns the delete/undo snackbar flow, delegates rendering to [HomeContent]. */
@Composable
fun HomeScreen(
    onOpenEinsatz: (String) -> Unit,
    onOpenSupport: (() -> Unit)?,
    onOpenSettings: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val deleteDoneText = stringResource(Res.string.home_delete_done)
    val undoText = stringResource(Res.string.home_delete_undo)

    HomeContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onQueryChange = viewModel::onQueryChange,
        onOpenEinsatz = onOpenEinsatz,
        onDeleteEinsatz = { einsatz ->
            viewModel.requestDelete(einsatz.id)
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = deleteDoneText,
                    actionLabel = undoText,
                    withDismissAction = false,
                    duration = SnackbarDuration.Long,
                )
                when (result) {
                    SnackbarResult.ActionPerformed -> viewModel.undoDelete()
                    SnackbarResult.Dismissed -> viewModel.commitPendingDeleteNow()
                }
            }
        },
        onCreateEinsatz = viewModel::createEinsatz,
        onSeedDemo = viewModel::seedDemo,
        onOpenSupport = onOpenSupport,
        onOpenSettings = onOpenSettings,
    )
}
