import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

// The presentation layer, with no platform services in it: theme, atomic
// components, the stateless `*Content` screens and the sample data their
// `@Preview`s use. `:composeApp` adds the ViewModels, DI, database and the
// store/foss seams on top.
//
// The extra `jvm()` target exists so this layer can be rendered headlessly —
// `:screenshots` composes store listing images from it (spec 008), and it is
// where the spec-007 component gallery will attach. Nothing here may depend on
// RevenueCat, Room's runtime or any `expect`/`actual` declaration, or that
// target stops resolving.
kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    jvm()

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(compose.runtime)
            api(compose.foundation)
            api(compose.material3)
            api(compose.ui)
            api(compose.components.resources)
            api(compose.components.uiToolingPreview)
            api(libs.compose.material.icons.core)

            api(libs.kotlinx.coroutines.core)
            api(libs.kotlinx.datetime)

            // Annotations only (`@Entity`, `@PrimaryKey`, …). The Room runtime
            // and the compiler stay in `:composeApp`, which owns the database.
            api(libs.room.common)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

compose.resources {
    // Consumed from `:composeApp` too, so the generated class must be public.
    publicResClass = true
    packageOfResClass = "einsatzlog.ui.generated.resources"
}

android {
    namespace = "de.einsatzlog.app.ui"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
