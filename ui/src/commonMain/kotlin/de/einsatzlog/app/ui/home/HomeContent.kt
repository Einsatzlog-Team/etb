package de.einsatzlog.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import de.einsatzlog.app.data.db.EinsatzEntity
import de.einsatzlog.app.ui.components.molecules.EinsatzListItem
import de.einsatzlog.app.ui.theme.Spacing
import einsatzlog.ui.generated.resources.Res
import einsatzlog.ui.generated.resources.app_name
import einsatzlog.ui.generated.resources.home_create_cancel
import einsatzlog.ui.generated.resources.home_create_confirm
import einsatzlog.ui.generated.resources.home_create_description_label
import einsatzlog.ui.generated.resources.home_create_name_label
import einsatzlog.ui.generated.resources.home_create_title
import einsatzlog.ui.generated.resources.home_delete
import einsatzlog.ui.generated.resources.home_demo_cta
import einsatzlog.ui.generated.resources.home_demo_name
import einsatzlog.ui.generated.resources.home_empty_body
import einsatzlog.ui.generated.resources.home_empty_cta
import einsatzlog.ui.generated.resources.home_empty_title
import einsatzlog.ui.generated.resources.home_no_results
import einsatzlog.ui.generated.resources.home_search_hint
import einsatzlog.ui.generated.resources.settings_title
import einsatzlog.ui.generated.resources.support_title
import org.jetbrains.compose.resources.stringResource

data class HomeUiState(
    val einsaetze: List<EinsatzEntity> = emptyList(),
    val entryCounts: Map<String, Int> = emptyMap(),
    val query: String = "",
    val loaded: Boolean = false,
    /** True when the unfiltered list is empty → first-run empty state. */
    val isEmpty: Boolean = false,
)

/** Home per spec 002 / sketch 01: searchable Einsatz list, active pinned, create FAB. Stateless. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    state: HomeUiState,
    snackbarHostState: SnackbarHostState,
    onQueryChange: (String) -> Unit,
    onOpenEinsatz: (String) -> Unit,
    onDeleteEinsatz: (EinsatzEntity) -> Unit,
    onCreateEinsatz: (String, String?) -> Unit,
    onSeedDemo: (String) -> Unit,
    /** Null hides the support entry (builds without in-app purchases). */
    onOpenSupport: (() -> Unit)?,
    onOpenSettings: () -> Unit,
    /** Incident shown in the detail pane (tablet layout); null on phones. */
    selectedEinsatzId: String? = null,
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    val demoName = stringResource(Res.string.home_demo_name)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.app_name)) },
                actions = {
                    if (onOpenSupport != null) {
                        IconButton(onClick = onOpenSupport) {
                            Icon(Icons.Filled.FavoriteBorder, contentDescription = stringResource(Res.string.support_title))
                        }
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = stringResource(Res.string.settings_title))
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { showCreateDialog = true }) {
                Text(stringResource(Res.string.home_empty_cta))
            }
        },
    ) { padding ->
        if (state.loaded && state.isEmpty) {
            // First run: explain the app, offer create + demo (spec 002).
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(Spacing.l),
                verticalArrangement = Arrangement.spacedBy(Spacing.m, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.home_empty_title),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(Res.string.home_empty_body),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedButton(onClick = { onSeedDemo(demoName) }) {
                    Text(stringResource(Res.string.home_demo_cta))
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = onQueryChange,
                    placeholder = { Text(stringResource(Res.string.home_search_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.m, vertical = Spacing.s),
                )
                if (state.loaded && state.einsaetze.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.home_no_results),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(Spacing.l),
                    )
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(Spacing.m),
                    verticalArrangement = Arrangement.spacedBy(Spacing.s),
                ) {
                    items(state.einsaetze, key = { it.id }) { einsatz ->
                        var menuOpen by remember { mutableStateOf(false) }
                        EinsatzListItem(
                            einsatz = einsatz,
                            entryCount = state.entryCounts[einsatz.id] ?: 0,
                            onClick = { onOpenEinsatz(einsatz.id) },
                            selected = einsatz.id == selectedEinsatzId,
                            onLongClick = { menuOpen = true },
                        )
                        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.home_delete)) },
                                onClick = {
                                    menuOpen = false
                                    onDeleteEinsatz(einsatz)
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        var name by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text(stringResource(Res.string.home_create_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.s)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(stringResource(Res.string.home_create_name_label)) },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(stringResource(Res.string.home_create_description_label)) },
                        singleLine = true,
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = name.isNotBlank(),
                    onClick = {
                        onCreateEinsatz(name, description.takeIf { it.isNotBlank() })
                        showCreateDialog = false
                    },
                ) { Text(stringResource(Res.string.home_create_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text(stringResource(Res.string.home_create_cancel))
                }
            },
        )
    }
}
