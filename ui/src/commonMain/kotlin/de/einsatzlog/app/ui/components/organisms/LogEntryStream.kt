package de.einsatzlog.app.ui.components.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.einsatzlog.app.data.db.LogEntryEntity
import de.einsatzlog.app.ui.components.molecules.LogEntryCard
import de.einsatzlog.app.ui.theme.Spacing
import einsatzlog.ui.generated.resources.Res
import einsatzlog.ui.generated.resources.logbook_empty
import org.jetbrains.compose.resources.stringResource

/** Organism: the chronological, color-coded entry stream (sketch 02), with empty state. */
@Composable
fun LogEntryStream(
    entries: List<LogEntryEntity>,
    loaded: Boolean,
    modifier: Modifier = Modifier,
) {
    if (loaded && entries.isEmpty()) {
        Text(
            text = stringResource(Res.string.logbook_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier.padding(Spacing.l).fillMaxWidth(),
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = Spacing.s),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            items(entries, key = { it.id }) { entry ->
                LogEntryCard(entry)
            }
        }
    }
}
