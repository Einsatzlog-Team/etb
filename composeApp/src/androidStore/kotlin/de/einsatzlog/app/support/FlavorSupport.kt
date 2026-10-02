package de.einsatzlog.app.support

/** Store flavor: the in-app tip via RevenueCat (`:purchases-revenuecat`). */
internal object FlavorSupport {
    const val SUPPORT_ENABLED = true

    fun repository(): SupportRepository =
        if (RevenueCatConfig.isConfigured) RevenueCatSupportRepository() else NoopSupportRepository()

    fun init(debugLogs: Boolean) = RevenueCatConfig.initIfConfigured(debugLogs)
}
