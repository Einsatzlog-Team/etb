package de.einsatzlog.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.savedstate.read
import de.einsatzlog.app.ui.entry.EntryFormScreen
import de.einsatzlog.app.ui.home.HomeScreen
import de.einsatzlog.app.ui.logbook.LogbookPane
import de.einsatzlog.app.ui.logbook.LogbookScreen
import de.einsatzlog.app.core.appVersionName
import de.einsatzlog.app.ui.settings.ImprintScreen
import de.einsatzlog.app.ui.settings.LicensesScreen
import de.einsatzlog.app.ui.settings.PrivacyScreen
import de.einsatzlog.app.ui.settings.SettingsScreen
import de.einsatzlog.app.ui.support.SupportScreen
import de.einsatzlog.app.ui.components.templates.EmptyDetail
import de.einsatzlog.app.ui.components.templates.LayoutClass
import de.einsatzlog.app.ui.components.templates.ListDetailTemplate
import de.einsatzlog.app.ui.components.templates.currentLayoutClass
import de.einsatzlog.app.ui.theme.EinsatzlogTheme
import einsatzlog.ui.generated.resources.Res
import einsatzlog.ui.generated.resources.home_select_incident
import org.jetbrains.compose.resources.stringResource

object Routes {
    const val HOME = "home"
    const val EINSATZ = "einsatz/{einsatzId}"
    const val NEW_ENTRY = "einsatz/{einsatzId}/new-entry"
    const val SUPPORT = "support"
    const val SETTINGS = "settings"
    const val PRIVACY = "settings/privacy"
    const val IMPRINT = "settings/imprint"
    const val LICENSES = "settings/licenses"
    fun einsatz(id: String) = "einsatz/$id"
    fun newEntry(id: String) = "einsatz/$id/new-entry"
}

/**
 * @param supportEnabled false hides every entry to the support screen — used by
 * the first iOS release, which ships without in-app purchases.
 */
@Composable
fun App(supportEnabled: Boolean = true) {
    EinsatzlogTheme {
        val navController = rememberNavController()
        val openSupport: (() -> Unit)? =
            if (supportEnabled) ({ navController.navigate(Routes.SUPPORT) }) else null
        NavHost(navController = navController, startDestination = Routes.HOME) {
            composable(Routes.HOME) {
                val layout = currentLayoutClass()
                if (layout == LayoutClass.COMPACT) {
                    HomeScreen(
                        onOpenEinsatz = { id -> navController.navigate(Routes.einsatz(id)) },
                        onOpenSupport = openSupport,
                        onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                    )
                } else {
                    // Tablet: list and logbook side by side, no navigation between them.
                    var selected by rememberSaveable { mutableStateOf<String?>(null) }
                    ListDetailTemplate(
                        list = {
                            HomeScreen(
                                onOpenEinsatz = { id -> selected = id },
                                onOpenSupport = openSupport,
                                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                            )
                        },
                        detail = {
                            val id = selected
                            if (id == null) {
                                EmptyDetail(stringResource(Res.string.home_select_incident))
                            } else {
                                key(id) {
                                    LogbookPane(
                                        einsatzId = id,
                                        layout = layout,
                                        onClose = { selected = null },
                                        onNewEntryFullScreen = { navController.navigate(Routes.newEntry(id)) },
                                    )
                                }
                            }
                        },
                    )
                }
            }
            composable(Routes.SUPPORT) {
                SupportScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    versionName = appVersionName,
                    onBack = { navController.popBackStack() },
                    onOpenSupport = openSupport,
                    onOpenPrivacy = { navController.navigate(Routes.PRIVACY) },
                    onOpenImprint = { navController.navigate(Routes.IMPRINT) },
                    onOpenLicenses = { navController.navigate(Routes.LICENSES) },
                )
            }
            composable(Routes.PRIVACY) { PrivacyScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.IMPRINT) { ImprintScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.LICENSES) { LicensesScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.EINSATZ) { backStackEntry ->
                val einsatzId = backStackEntry.arguments?.read { getStringOrNull("einsatzId") }
                    ?: return@composable
                LogbookScreen(
                    einsatzId = einsatzId,
                    onBack = { navController.popBackStack() },
                    onNewEntry = { navController.navigate(Routes.newEntry(einsatzId)) },
                )
            }
            composable(Routes.NEW_ENTRY) { backStackEntry ->
                val einsatzId = backStackEntry.arguments?.read { getStringOrNull("einsatzId") }
                    ?: return@composable
                EntryFormScreen(
                    einsatzId = einsatzId,
                    onDone = { navController.popBackStack() },
                )
            }
        }
    }
}
