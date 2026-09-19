package de.einsatzlog.app.ui.components.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import de.einsatzlog.app.core.format.formatTime
import de.einsatzlog.app.data.db.LogEntryEntity
import de.einsatzlog.app.ui.components.atoms.TypeChip
import de.einsatzlog.app.ui.theme.Spacing

/** Molecule: one row of the entry stream (sketch 02) — tabular time, type color, Von→Zu, message. */
@Composable
fun LogEntryCard(entry: LogEntryEntity, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.fillMaxWidth(), tonalElevation = 1.dp) {
        Column(modifier = Modifier.padding(horizontal = Spacing.m, vertical = Spacing.s)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            ) {
                Text(
                    text = formatTime(entry.timestampEpochMs),
                    style = MaterialTheme.typography.labelLarge,
                    fontFamily = FontFamily.Monospace,
                )
                TypeChip(entry.type)
                val fromTo = listOfNotNull(entry.source, entry.target)
                if (fromTo.isNotEmpty()) {
                    Text(
                        text = fromTo.joinToString(" → "),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Text(
                text = entry.message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = Spacing.xs),
            )
        }
    }
}
