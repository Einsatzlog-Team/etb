package de.einsatzlog.app.ui.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.rememberLibraries
import einsatzlog.ui.generated.resources.Res
import einsatzlog.ui.generated.resources.settings_licenses
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

/**
 * Stays in `:composeApp` — the only screen with a non-UI dependency
 * (AboutLibraries) and the generated `aboutlibraries.json` it reads.
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun LicensesScreen(onBack: () -> Unit) {
    SettingsScaffold(title = stringResource(Res.string.settings_licenses), onBack = onBack) {
        val libraries by rememberLibraries {
            Res.readBytes("files/aboutlibraries.json").decodeToString()
        }
        LibrariesContainer(libraries, modifier = Modifier.fillMaxSize())
    }
}
