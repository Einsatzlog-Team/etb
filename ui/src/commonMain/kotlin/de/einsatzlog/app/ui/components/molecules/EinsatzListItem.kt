package de.einsatzlog.app.ui.components.molecules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import de.einsatzlog.app.core.format.formatDateTime
import de.einsatzlog.app.data.db.EinsatzEntity
import de.einsatzlog.app.ui.components.atoms.ActiveBadge
import de.einsatzlog.app.ui.theme.Spacing
import einsatzlog.ui.generated.resources.Res
import einsatzlog.ui.generated.resources.einsatz_entry_count
import org.jetbrains.compose.resources.pluralStringResource

/**
 * Molecule: one Einsatz row on Home (sketch 01) — name, start, entry count, active state.
 * [selected] marks the incident open in the detail pane of the tablet layout.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EinsatzListItem(
    einsatz: EinsatzEntity,
    entryCount: Int,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    // Selection = outline in the brand colour on a slightly stronger surface – deliberately
    // not a red fill, so it never competes with the message-type colours (design-system.md).
    Card(
        modifier = modifier.fillMaxWidth().semantics { this.selected = selected },
        colors = if (selected) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        } else {
            CardDefaults.cardColors()
        },
        border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
    ) {
        Row(
            modifier = Modifier
                .combinedClickable(onClick = onClick, onLongClick = onLongClick)
                .padding(Spacing.m),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.m),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(einsatz.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = formatDateTime(einsatz.startedAtEpochMs) + " · " +
                        pluralStringResource(Res.plurals.einsatz_entry_count, entryCount, entryCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (einsatz.endedAtEpochMs == null) {
                ActiveBadge()
            }
        }
    }
}
