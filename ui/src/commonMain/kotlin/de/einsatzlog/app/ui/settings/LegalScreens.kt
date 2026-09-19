package de.einsatzlog.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import de.einsatzlog.app.ui.theme.Spacing
import einsatzlog.ui.generated.resources.Res
import einsatzlog.ui.generated.resources.imprint_body
import einsatzlog.ui.generated.resources.privacy_body
import einsatzlog.ui.generated.resources.privacy_link
import einsatzlog.ui.generated.resources.settings_imprint
import einsatzlog.ui.generated.resources.settings_privacy
import org.jetbrains.compose.resources.stringResource

@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    SettingsScaffold(title = stringResource(Res.string.settings_privacy), onBack = onBack) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(Spacing.m),
            verticalArrangement = Arrangement.spacedBy(Spacing.m),
        ) {
            Text(stringResource(Res.string.privacy_body), style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = { uriHandler.openUri("https://einsatzlog.de/privacy") }) {
                Text(stringResource(Res.string.privacy_link))
            }
        }
    }
}

@Composable
fun ImprintScreen(onBack: () -> Unit) {
    SettingsScaffold(title = stringResource(Res.string.settings_imprint), onBack = onBack) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(Spacing.m),
        ) {
            Text(stringResource(Res.string.imprint_body), style = MaterialTheme.typography.bodyMedium)
        }
    }
}
