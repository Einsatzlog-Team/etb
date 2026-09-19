package de.einsatzlog.app.core

import platform.Foundation.NSBundle

actual val appVersionName: String by lazy {
    val info = NSBundle.mainBundle.infoDictionary
    val version = info?.get("CFBundleShortVersionString") as? String ?: "?"
    val build = info?.get("CFBundleVersion") as? String ?: "?"
    "$version ($build)"
}
