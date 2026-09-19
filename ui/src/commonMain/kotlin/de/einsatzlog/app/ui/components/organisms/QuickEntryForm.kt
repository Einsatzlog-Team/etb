package de.einsatzlog.app.ui.components.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import de.einsatzlog.app.core.format.formatTime
import de.einsatzlog.app.domain.MessageType
import de.einsatzlog.app.ui.components.molecules.EntryTypeSelector
import de.einsatzlog.app.ui.components.molecules.SuggestionChipRow
import de.einsatzlog.app.ui.theme.Spacing
import einsatzlog.ui.generated.resources.Res
import einsatzlog.ui.generated.resources.entry_backdate_1
import einsatzlog.ui.generated.resources.entry_backdate_5
import einsatzlog.ui.generated.resources.entry_backdate_reset
import einsatzlog.ui.generated.resources.entry_message_label
import einsatzlog.ui.generated.resources.entry_source_label
import einsatzlog.ui.generated.resources.entry_target_label
import einsatzlog.ui.generated.resources.entry_time_label
import org.jetbrains.compose.resources.stringResource

/**
 * Organism: the quick-entry form body (spec 003 / sketch 03) — auto time with
 * backdate chips, one-tap type row, Von/Zu with suggestions, message. Stateless:
 * all values in, all edits out via callbacks.
 */
@Composable
fun QuickEntryForm(
    timestampEpochMs: Long,
    type: MessageType,
    source: String,
    target: String,
    message: String,
    sourceSuggestions: List<String>,
    targetSuggestions: List<String>,
    onType: (MessageType) -> Unit,
    onSource: (String) -> Unit,
    onTarget: (String) -> Unit,
    onMessage: (String) -> Unit,
    onBackdate: (Int) -> Unit,
    onResetTime: () -> Unit,
    modifier: Modifier = Modifier,
    messageFocusRequester: FocusRequester? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.m),
    ) {
        // Zeit: auto, not editable — corrections only via backdate chips.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        ) {
            Text(
                text = stringResource(Res.string.entry_time_label) + " " + formatTime(timestampEpochMs),
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily.Monospace,
            )
            AssistChip(onClick = { onBackdate(1) }, label = { Text(stringResource(Res.string.entry_backdate_1)) })
            AssistChip(onClick = { onBackdate(5) }, label = { Text(stringResource(Res.string.entry_backdate_5)) })
            AssistChip(onClick = onResetTime, label = { Text(stringResource(Res.string.entry_backdate_reset)) })
        }

        EntryTypeSelector(selected = type, onSelect = onType)

        OutlinedTextField(
            value = source,
            onValueChange = onSource,
            label = { Text(stringResource(Res.string.entry_source_label)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
        )
        SuggestionChipRow(suggestions = sourceSuggestions, onPick = onSource)

        OutlinedTextField(
            value = target,
            onValueChange = onTarget,
            label = { Text(stringResource(Res.string.entry_target_label)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
        )
        SuggestionChipRow(suggestions = targetSuggestions, onPick = onTarget)

        OutlinedTextField(
            value = message,
            onValueChange = onMessage,
            label = { Text(stringResource(Res.string.entry_message_label)) },
            minLines = 3,
            modifier = Modifier.fillMaxWidth().let {
                if (messageFocusRequester != null) it.focusRequester(messageFocusRequester) else it
            },
        )
    }
}
