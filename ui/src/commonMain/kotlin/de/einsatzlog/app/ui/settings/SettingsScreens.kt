package de.einsatzlog.app.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import de.einsatzlog.app.ui.theme.Spacing
import einsatzlog.ui.generated.resources.Res
import einsatzlog.ui.generated.resources.app_name
import einsatzlog.ui.generated.resources.logbook_back
import einsatzlog.ui.generated.resources.settings_contact
import einsatzlog.ui.generated.resources.settings_imprint
import einsatzlog.ui.generated.resources.settings_legal
import einsatzlog.ui.generated.resources.settings_licenses
import einsatzlog.ui.generated.resources.settings_privacy
import einsatzlog.ui.generated.resources.settings_support
import einsatzlog.ui.generated.resources.settings_title
import einsatzlog.ui.generated.resources.settings_version
import einsatzlog.ui.generated.resources.settings_website
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** Shared scaffold for the settings family of screens. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.logbook_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) { content() }
    }
}

/**
 * Stateless — [versionName] is passed in rather than read from the platform
 * `appVersionName`, so the screenshot pipeline can pin a stable version string.
 */
@Composable
fun SettingsScreen(
    versionName: String,
    onBack: () -> Unit,
    onOpenSupport: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenImprint: () -> Unit,
    onOpenLicenses: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    SettingsScaffold(title = stringResource(Res.string.settings_title), onBack = onBack) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(Spacing.m),
            verticalArrangement = Arrangement.spacedBy(Spacing.m),
        ) {
            Card {
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.app_name)) },
                    supportingContent = {
                        Text(stringResource(Res.string.settings_version) + " " + versionName)
                    },
                )
            }
            Card(modifier = Modifier.fillMaxWidth()) {
                SettingsLink(Res.string.settings_support, onOpenSupport)
                HorizontalDivider()
                SettingsLink(Res.string.settings_website) { uriHandler.openUri("https://einsatzlog.de") }
                HorizontalDivider()
                SettingsLink(Res.string.settings_contact) { uriHandler.openUri("mailto:kontakt@einsatzlog.de") }
            }
            Text(
                text = stringResource(Res.string.settings_legal),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                SettingsLink(Res.string.settings_privacy, onOpenPrivacy)
                HorizontalDivider()
                SettingsLink(Res.string.settings_imprint, onOpenImprint)
                HorizontalDivider()
                SettingsLink(Res.string.settings_licenses, onOpenLicenses)
            }
        }
    }
}

@Composable
private fun SettingsLink(label: StringResource, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(stringResource(label)) },
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    )
}
