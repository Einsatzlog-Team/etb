package de.einsatzlog.app.ui.components.molecules

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.einsatzlog.app.domain.MessageType
import de.einsatzlog.app.ui.theme.LocalEntryTypeColors
import de.einsatzlog.app.ui.theme.Spacing

/**
 * Molecule: one-tap type selection in a single horizontally scrollable row
 * (predecessor lesson: a vertical chip stack ate a full phone screen).
 * Color coding matches the stream; glove-friendly 48dp chips.
 */
@Composable
fun EntryTypeSelector(
    selected: MessageType,
    onSelect: (MessageType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val typeColors = LocalEntryTypeColors.current
    Row(
        modifier = modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        MessageType.entries.forEach { type ->
            val colors = typeColors[type]
            FilterChip(
                selected = selected == type,
                onClick = { onSelect(type) },
                label = { Text(type.labelDe) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colors.container,
                    selectedLabelColor = colors.onContainer,
                ),
                modifier = Modifier.height(Spacing.touchTarget),
            )
        }
    }
}
