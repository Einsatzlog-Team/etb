package de.einsatzlog.app.ui.entry

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import de.einsatzlog.app.domain.MessageType
import de.einsatzlog.app.ui.components.organisms.QuickEntryForm
import de.einsatzlog.app.ui.theme.Spacing
import einsatzlog.ui.generated.resources.Res
import einsatzlog.ui.generated.resources.entry_submit
import einsatzlog.ui.generated.resources.entry_title
import einsatzlog.ui.generated.resources.logbook_back
import org.jetbrains.compose.resources.stringResource

data class EntryFormState(
    /** The moment "Neuer Eintrag" was tapped — when the message arrived (spec 003). */
    val timestampEpochMs: Long,
    val type: MessageType = MessageType.FUNKSPRUCH,
    val source: String = "",
    val target: String = "",
    val message: String = "",
    val sourceSuggestions: List<String> = emptyList(),
    val targetSuggestions: List<String> = emptyList(),
    val submitting: Boolean = false,
) {
    val canSubmit: Boolean get() = message.isNotBlank() && !submitting
}

/** Stateless quick-entry screen: scaffold + sticky submit around the [QuickEntryForm] organism. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryFormContent(
    state: EntryFormState,
    onType: (MessageType) -> Unit,
    onSource: (String) -> Unit,
    onTarget: (String) -> Unit,
    onMessage: (String) -> Unit,
    onBackdate: (Int) -> Unit,
    onResetTime: () -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
) {
    val messageFocus = remember { FocusRequester() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.entry_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.logbook_back),
                        )
                    }
                },
            )
        },
        bottomBar = {
            // Sticky, keyboard-aware submit — never below the fold (predecessor lesson).
            Button(
                onClick = onSubmit,
                enabled = state.canSubmit,
                modifier = Modifier.fillMaxWidth()
                    .imePadding()
                    .padding(Spacing.m)
                    .height(Spacing.touchTarget + Spacing.s),
            ) {
                Text(stringResource(Res.string.entry_submit), style = MaterialTheme.typography.titleMedium)
            }
        },
    ) { padding ->
        QuickEntryForm(
            timestampEpochMs = state.timestampEpochMs,
            type = state.type,
            source = state.source,
            target = state.target,
            message = state.message,
            sourceSuggestions = state.sourceSuggestions,
            targetSuggestions = state.targetSuggestions,
            onType = onType,
            onSource = onSource,
            onTarget = onTarget,
            onMessage = onMessage,
            onBackdate = onBackdate,
            onResetTime = onResetTime,
            messageFocusRequester = messageFocus,
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.m),
        )
    }
}
