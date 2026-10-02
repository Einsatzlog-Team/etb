package de.einsatzlog.app

import androidx.compose.ui.window.ComposeUIViewController
import de.einsatzlog.app.di.appModule
import de.einsatzlog.app.di.platformModule
import de.einsatzlog.app.support.NoopSupportRepository
import de.einsatzlog.app.support.RevenueCatConfig
import de.einsatzlog.app.support.RevenueCatSupportRepository
import org.koin.core.context.startKoin
import platform.UIKit.UIViewController

// 0.1.4 (the first iOS release) shipped with this false: no support screen,
// RevenueCat never configured. 0.1.5 brings the in-app purchase back.
private const val IOS_PURCHASES_ENABLED = true

fun initKoin() {
    val purchases = IOS_PURCHASES_ENABLED && RevenueCatConfig.isConfigured
    startKoin {
        modules(platformModule, appModule { if (purchases) RevenueCatSupportRepository() else NoopSupportRepository() })
    }
    if (IOS_PURCHASES_ENABLED) RevenueCatConfig.initIfConfigured()
}

@Suppress("unused", "FunctionName") // called from Swift
fun MainViewController(): UIViewController = ComposeUIViewController { App(supportEnabled = IOS_PURCHASES_ENABLED) }
