package de.einsatzlog.app.ui.components.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.einsatzlog.app.ui.components.templates.EmptyDetail
import de.einsatzlog.app.ui.components.templates.ListDetailTemplate
import de.einsatzlog.app.ui.components.templates.StreamWithEntryPaneTemplate
import de.einsatzlog.app.ui.theme.EinsatzlogTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
private fun Slot(name: String) = Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(name) }

@Preview(widthDp = 1280, heightDp = 800)
@Composable
private fun ListDetailTemplatePreview() = EinsatzlogTheme(darkTheme = false) {
    ListDetailTemplate(list = { Slot("incident list") }, detail = { EmptyDetail("Select an incident on the left.") })
}

@Preview(widthDp = 920, heightDp = 800)
@Composable
private fun StreamWithEntryPaneTemplatePreview() = EinsatzlogTheme(darkTheme = true) {
    StreamWithEntryPaneTemplate(stream = { Slot("logbook stream") }, pane = { Slot("entry pane") })
}
