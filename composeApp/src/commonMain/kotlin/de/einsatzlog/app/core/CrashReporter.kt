package de.einsatzlog.app.core

/**
 * Seam for crash reporting. `store` flavor binds Crashlytics (slice 5);
 * `foss` flavor and iOS v1 stay on [NoopCrashReporter].
 */
interface CrashReporter {
    fun log(message: String)
    fun recordException(throwable: Throwable)
}

object NoopCrashReporter : CrashReporter {
    override fun log(message: String) = Unit
    override fun recordException(throwable: Throwable) = Unit
}
