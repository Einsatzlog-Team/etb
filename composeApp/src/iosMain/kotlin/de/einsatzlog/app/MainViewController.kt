package de.einsatzlog.app

import androidx.compose.ui.window.ComposeUIViewController
import de.einsatzlog.app.di.appModule
import de.einsatzlog.app.di.platformModule
import de.einsatzlog.app.support.RevenueCatConfig
import org.koin.core.context.startKoin
import platform.UIKit.UIViewController

fun initKoin() {
    startKoin {
        modules(platformModule, appModule(purchasesEnabled = true))
    }
    RevenueCatConfig.initIfConfigured()
}

@Suppress("unused", "FunctionName") // called from Swift
fun MainViewController(): UIViewController = ComposeUIViewController { App() }
