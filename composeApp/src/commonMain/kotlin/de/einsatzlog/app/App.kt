package de.einsatzlog.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.savedstate.read
import de.einsatzlog.app.ui.entry.EntryFormScreen
import de.einsatzlog.app.ui.home.HomeScreen
import de.einsatzlog.app.ui.logbook.LogbookScreen
import de.einsatzlog.app.core.appVersionName
import de.einsatzlog.app.ui.settings.ImprintScreen
import de.einsatzlog.app.ui.settings.LicensesScreen
import de.einsatzlog.app.ui.settings.PrivacyScreen
import de.einsatzlog.app.ui.settings.SettingsScreen
import de.einsatzlog.app.ui.support.SupportScreen
import de.einsatzlog.app.ui.theme.EinsatzlogTheme

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

@Composable
fun App() {
    EinsatzlogTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = Routes.HOME) {
            composable(Routes.HOME) {
                HomeScreen(
                    onOpenEinsatz = { id -> navController.navigate(Routes.einsatz(id)) },
                    onOpenSupport = { navController.navigate(Routes.SUPPORT) },
                    onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                )
            }
            composable(Routes.SUPPORT) {
                SupportScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    versionName = appVersionName,
                    onBack = { navController.popBackStack() },
                    onOpenSupport = { navController.navigate(Routes.SUPPORT) },
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
