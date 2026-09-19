@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    application
}

// Store listing images, rendered headlessly from `:ui` (spec 008).
//
// This never launches an emulator or a simulator: it composes the real screens
// onto an off-screen Skia surface sized in exact store pixels. Compose
// Multiplatform draws Android and iOS through the same Skia stack, so one run
// produces both stores' assets, on any OS, in CI.
//
// Deliberately NOT part of `check` — it writes files, it does not assert.
// Visual regression is a separate concern (Roborazzi, spec 007 follow-up).
kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(projects.ui)
    implementation(compose.desktop.currentOs)
    // Drives composition to idle before capture, so asynchronously-loaded
    // string resources are actually on screen when the frame is grabbed.
    implementation(compose.uiTest)
}

application {
    mainClass.set("de.einsatzlog.screenshots.MainKt")
    applicationDefaultJvmArgs = listOf("-Djava.awt.headless=true")
}

/** `./gradlew :screenshots:screenshots` — regenerates every store image. */
tasks.register("screenshots") {
    group = "store"
    description = "Renders Play and App Store listing images into build/store."
    dependsOn(tasks.named("run"))
}
