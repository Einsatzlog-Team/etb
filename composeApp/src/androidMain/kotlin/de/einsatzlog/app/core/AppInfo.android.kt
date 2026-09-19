package de.einsatzlog.app.core

import de.einsatzlog.app.BuildConfig

actual val appVersionName: String = BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")"
