package de.einsatzlog.app

import androidx.compose.ui.window.ComposeUIViewController
import de.einsatzlog.app.di.appModule
import de.einsatzlog.app.di.platformModule
import de.einsatzlog.app.support.RevenueCatConfig
import org.koin.core.context.startKoin
import platform.UIKit.UIViewController

// The first iOS release ships without in-app purchases: no support screen,
// RevenueCat never configured. Flip to true to bring the support screen back.
private const val IOS_PURCHASES_ENABLED = false

fun initKoin() {
    startKoin {
        modules(platformModule, appModule(purchasesEnabled = IOS_PURCHASES_ENABLED))
    }
    if (IOS_PURCHASES_ENABLED) RevenueCatConfig.initIfConfigured()
}

@Suppress("unused", "FunctionName") // called from Swift
fun MainViewController(): UIViewController = ComposeUIViewController { App(supportEnabled = IOS_PURCHASES_ENABLED) }
