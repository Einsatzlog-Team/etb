package de.einsatzlog.app.ui.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import einsatzlog.ui.generated.resources.Res
import einsatzlog.ui.generated.resources.support_badge
import org.jetbrains.compose.resources.stringResource

/** Atom: gold supporter badge — shown when the `supporter` entitlement is active. */
@Composable
fun SupporterBadge(modifier: Modifier = Modifier) {
    Text(
        text = "★ " + stringResource(Res.string.support_badge),
        style = MaterialTheme.typography.labelMedium,
        color = Color(0xFF3F2E00),
        modifier = modifier
            .background(Color(0xFFF5C518), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 3.dp),
    )
}
