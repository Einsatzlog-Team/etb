package de.einsatzlog.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.einsatzlog.app.domain.MessageType

/**
 * Particle level: design tokens (design/design-system.md).
 * Feature code consumes these — never raw MaterialTheme values — so the
 * Material base stays swappable (design/ui-library-evaluation.md).
 */
object Spacing {
    val xs = 4.dp
    val s = 8.dp
    val m = 16.dp
    val l = 24.dp
    val xl = 32.dp

    /** Glove-friendly minimum touch target (design principle 1). */
    val touchTarget = 48.dp
}

@Immutable
data class TypeColorPair(val container: Color, val onContainer: Color)

/** Message-type color coding — the primary scanning aid in the entry stream. */
@Immutable
data class EntryTypeColors(
    val funkspruch: TypeColorPair,
    val lagemeldung: TypeColorPair,
    val auftrag: TypeColorPair,
    val anforderung: TypeColorPair,
    val dokumentation: TypeColorPair,
) {
    operator fun get(type: MessageType): TypeColorPair = when (type) {
        MessageType.FUNKSPRUCH -> funkspruch
        MessageType.LAGEMELDUNG -> lagemeldung
        MessageType.AUFTRAG -> auftrag
        MessageType.ANFORDERUNG -> anforderung
        MessageType.DOKUMENTATION -> dokumentation
    }
}

val LightEntryTypeColors = EntryTypeColors(
    funkspruch = TypeColorPair(Color(0xFFD6E3FF), Color(0xFF001B3D)),
    lagemeldung = TypeColorPair(Color(0xFFFFDF9E), Color(0xFF261A00)),
    auftrag = TypeColorPair(Color(0xFFC9EFC1), Color(0xFF072100)),
    anforderung = TypeColorPair(Color(0xFFEADDFF), Color(0xFF21005D)),
    dokumentation = TypeColorPair(Color(0xFFE2E2E7), Color(0xFF1A1C1E)),
)

val DarkEntryTypeColors = EntryTypeColors(
    funkspruch = TypeColorPair(Color(0xFF284777), Color(0xFFD6E3FF)),
    lagemeldung = TypeColorPair(Color(0xFF5C4300), Color(0xFFFFDF9E)),
    auftrag = TypeColorPair(Color(0xFF275024), Color(0xFFC9EFC1)),
    anforderung = TypeColorPair(Color(0xFF4F378B), Color(0xFFEADDFF)),
    dokumentation = TypeColorPair(Color(0xFF45474A), Color(0xFFE2E2E7)),
)

val LocalEntryTypeColors = staticCompositionLocalOf { LightEntryTypeColors }
