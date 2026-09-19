package de.einsatzlog.app.ui.components.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.einsatzlog.app.core.format.formatDateTime
import de.einsatzlog.app.data.db.EinsatzEntity
import de.einsatzlog.app.ui.components.atoms.ActiveBadge
import de.einsatzlog.app.ui.theme.Spacing
import einsatzlog.ui.generated.resources.Res
import einsatzlog.ui.generated.resources.logbook_closed
import einsatzlog.ui.generated.resources.logbook_ended_at
import einsatzlog.ui.generated.resources.logbook_started_at
import org.jetbrains.compose.resources.stringResource

/** Organism: the Einsatz header card on the logbook screen (sketch 02) — start/end, status, description. */
@Composable
fun EinsatzHeader(
    einsatz: EinsatzEntity,
    isClosed: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = Spacing.m, vertical = Spacing.s),
    ) {
        Column(
            modifier = Modifier.padding(Spacing.m),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.logbook_started_at) + " " +
                        formatDateTime(einsatz.startedAtEpochMs),
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (!isClosed) ActiveBadge()
            }
            einsatz.endedAtEpochMs?.let { ended ->
                Text(
                    text = stringResource(Res.string.logbook_ended_at) + " " + formatDateTime(ended),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = stringResource(Res.string.logbook_closed),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            einsatz.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
