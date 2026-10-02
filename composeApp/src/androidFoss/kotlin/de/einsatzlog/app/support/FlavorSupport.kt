package de.einsatzlog.app.support

/**
 * FOSS flavor: no purchases. This source set cannot see `:purchases-revenuecat`
 * (only `store` links it), so no proprietary code ends up in this build.
 */
internal object FlavorSupport {
    const val SUPPORT_ENABLED = false

    fun repository(): SupportRepository = NoopSupportRepository()

    fun init(debugLogs: Boolean) = Unit
}
