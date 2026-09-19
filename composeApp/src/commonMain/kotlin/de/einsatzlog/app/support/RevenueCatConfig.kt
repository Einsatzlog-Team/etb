package de.einsatzlog.app.support

import com.revenuecat.purchases.kmp.LogLevel
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesConfiguration

/** RevenueCat *public* SDK key for this platform — safe to commit (spec 004). */
expect val revenueCatApiKey: String

object RevenueCatConfig {
    private const val PLACEHOLDER_MARKER = "PLACEHOLDER"

    /** False until the real project keys replace the placeholders. */
    val isConfigured: Boolean get() = !revenueCatApiKey.contains(PLACEHOLDER_MARKER)

    /**
     * Configure the SDK. Callers gate on flavor (Android `store` only) —
     * with placeholder keys this is a no-op, so the app works before the
     * RevenueCat project exists.
     */
    fun initIfConfigured(debugLogs: Boolean = false) {
        if (!isConfigured) return
        if (debugLogs) Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(PurchasesConfiguration(apiKey = revenueCatApiKey))
    }
}
