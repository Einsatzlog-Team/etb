package de.einsatzlog.app

import android.app.Application
import de.einsatzlog.app.di.appModule
import de.einsatzlog.app.di.platformModule
import de.einsatzlog.app.support.RevenueCatConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class EinsatzlogApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val purchasesEnabled = BuildConfig.FLAVOR == "store"
        startKoin {
            androidContext(this@EinsatzlogApplication)
            modules(platformModule, appModule(purchasesEnabled))
        }
        if (purchasesEnabled) RevenueCatConfig.initIfConfigured(debugLogs = BuildConfig.DEBUG)
    }
}
