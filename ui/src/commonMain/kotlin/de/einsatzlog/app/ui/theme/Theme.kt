package de.einsatzlog.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

// Fire-service red used sparingly (brand + destructive), neutral surfaces otherwise.
// No Android dynamic color — type coding must stay consistent (ui-library-evaluation.md).
private val LightColors = lightColorScheme(
    primary = Color(0xFFB3261E),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDAD5),
    onPrimaryContainer = Color(0xFF410001),
    secondary = Color(0xFF775652),
    onSecondary = Color(0xFFFFFFFF),
    surface = Color(0xFFFCF8F8),
    onSurface = Color(0xFF1C1B1B),
    surfaceVariant = Color(0xFFF0DEDC),
    onSurfaceVariant = Color(0xFF534341),
    error = Color(0xFFBA1A1A),
)

// Dark tuned for night operations: no pure white, subdued large surfaces.
private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB4AA),
    onPrimary = Color(0xFF690003),
    primaryContainer = Color(0xFF93000A),
    onPrimaryContainer = Color(0xFFFFDAD5),
    secondary = Color(0xFFE7BDB7),
    onSecondary = Color(0xFF442926),
    surface = Color(0xFF141313),
    onSurface = Color(0xFFE5E2E1),
    surfaceVariant = Color(0xFF534341),
    onSurfaceVariant = Color(0xFFD8C2BF),
    error = Color(0xFFFFB4AB),
)

@Composable
fun EinsatzlogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalEntryTypeColors provides if (darkTheme) DarkEntryTypeColors else LightEntryTypeColors,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            content = content,
        )
    }
}
