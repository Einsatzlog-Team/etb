import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.aboutlibraries)
}

// This repository starts from a code-only snapshot (no prior history), so the
// version is pinned here instead of being derived from git. Bump both on each
// release; CI/local builds may still override with -PversionCode / -PversionName:
//   ./gradlew :composeApp:bundleStoreRelease -PversionCode=42
val gitVersionCode: Int = (findProperty("versionCode") as String?)?.toIntOrNull() ?: 19
val gitVersionName: String = (findProperty("versionName") as String?) ?: "0.1.2"

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Presentation layer — also brings the Compose artifacts, the
            // generated resources and the shared UI types along transitively.
            implementation(projects.ui)

            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

            implementation(libs.purchases.kmp.core)
            implementation(libs.purchases.kmp.ui)
            implementation(libs.aboutlibraries.compose.m3)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.jb.lifecycle.viewmodel.compose)
            implementation(libs.jb.lifecycle.runtime.compose)
            implementation(libs.jb.navigation.compose)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

android {
    namespace = "de.einsatzlog.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "de.einsatzlog.app"
        minSdk = 26
        targetSdk = 36
        versionCode = gitVersionCode
        versionName = gitVersionName
    }

    // Reads a gitignored keystore.properties when present (Michal keeps
    // custody); falls back to debug signing so release always builds.
    val keystorePropsFile = rootProject.file("keystore.properties")
    if (keystorePropsFile.exists()) {
        val props = Properties().apply { keystorePropsFile.inputStream().use { load(it) } }
        signingConfigs.create("release") {
            storeFile = rootProject.file(props.getProperty("storeFile"))
            storePassword = props.getProperty("storePassword")
            keyAlias = props.getProperty("keyAlias")
            keyPassword = props.getProperty("keyPassword")
        }
    }

    // `store` = Play/App Store build (Crashlytics + RevenueCat land here in later slices).
    // `foss` = no proprietary services, F-Droid-compatible (see specs/004 for the seam strategy).
    flavorDimensions += "distribution"
    productFlavors {
        create("store") {
            dimension = "distribution"
            isDefault = true
        }
        create("foss") {
            dimension = "distribution"
            applicationIdSuffix = ".foss"
            versionNameSuffix = "-foss"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.findByName("release") ?: signingConfigs.getByName("debug")
        }
    }

    buildFeatures {
        buildConfig = true // flavor detection for the purchases seam (spec 004)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}
