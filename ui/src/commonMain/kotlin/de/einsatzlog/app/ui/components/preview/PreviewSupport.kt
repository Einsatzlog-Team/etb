package de.einsatzlog.app.ui.components.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.einsatzlog.app.data.db.EinsatzEntity
import de.einsatzlog.app.data.db.LogEntryEntity
import de.einsatzlog.app.domain.MessageType
import de.einsatzlog.app.ui.theme.EinsatzlogTheme

/** Version string used by the settings previews — pinned so previews never churn. */
const val PREVIEW_VERSION: String = "1.0.0"

/**
 * Themed container for atomic-design component previews (spec 007). Each
 * component gets a light and a dark preview so the gallery documents both
 * themes (design-system.md rule). These live in `:ui` for IDE preview and
 * Artboard discovery, and no longer compile into the shipped app.
 */
@Composable
internal fun PreviewSurface(
    dark: Boolean,
    width: Int? = null,
    content: @Composable () -> Unit,
) {
    EinsatzlogTheme(darkTheme = dark) {
        Surface {
            Box(Modifier.padding(16.dp).let { if (width != null) it.width(width.dp) else it }) {
                content()
            }
        }
    }
}

// --- Sample data (fixed timestamps → deterministic previews) ---
// Public because `:screenshots` builds the store listing images from exactly
// this data — the screenshots and the previews can then never disagree.

val previewEinsatzActive = EinsatzEntity(
    id = "e1",
    name = "Übung Brandhaus",
    description = "Jahresübung der Feuerwehr",
    startedAtEpochMs = 1_752_998_400_000,
    endedAtEpochMs = null,
    createdAtEpochMs = 1_752_998_400_000,
)

val previewEinsatzClosed = previewEinsatzActive.copy(
    id = "e2",
    name = "Ölspur B3",
    endedAtEpochMs = 1_753_005_600_000,
)

fun previewEntry(
    type: MessageType,
    message: String,
    source: String? = "ELW",
    target: String? = "Leitstelle",
) = LogEntryEntity(
    id = "l-${type.name}",
    einsatzId = "e1",
    timestampEpochMs = 1_752_998_700_000,
    type = type,
    source = source,
    target = target,
    message = message,
    createdAtEpochMs = 1_752_998_700_000,
)
