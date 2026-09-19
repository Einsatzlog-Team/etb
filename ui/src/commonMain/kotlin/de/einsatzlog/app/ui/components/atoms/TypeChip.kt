package de.einsatzlog.app.ui.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.einsatzlog.app.domain.MessageType
import de.einsatzlog.app.ui.theme.LocalEntryTypeColors

/** Atom: color-coded message-type label — the primary scanning aid in the stream. */
@Composable
fun TypeChip(type: MessageType, modifier: Modifier = Modifier) {
    val colors = LocalEntryTypeColors.current[type]
    Text(
        text = type.labelDe,
        style = MaterialTheme.typography.labelMedium,
        color = colors.onContainer,
        modifier = modifier
            .background(colors.container, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp),
    )
}
