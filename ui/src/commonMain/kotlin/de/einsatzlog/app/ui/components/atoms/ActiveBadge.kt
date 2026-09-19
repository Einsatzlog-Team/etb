package de.einsatzlog.app.ui.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import einsatzlog.ui.generated.resources.Res
import einsatzlog.ui.generated.resources.einsatz_active
import org.jetbrains.compose.resources.stringResource

/** Atom: marks an active (open) Einsatz in lists and headers. */
@Composable
fun ActiveBadge(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(Res.string.einsatz_active),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = modifier
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp),
    )
}
