package de.einsatzlog.app

import android.app.Application
import de.einsatzlog.app.di.appModule
import de.einsatzlog.app.di.platformModule
import de.einsatzlog.app.support.FlavorSupport
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class EinsatzlogApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@EinsatzlogApplication)
            modules(platformModule, appModule { FlavorSupport.repository() })
        }
        FlavorSupport.init(debugLogs = BuildConfig.DEBUG)
    }
}
