plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    kotlin("plugin.compose")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "zero.friends.shared"
    compileSdk = libs.versions.compileSdk.get().toInt()

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "zero.friends.gostopcalculator.shared.generated.resources"
}

kotlin {
    compilerOptions {
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_4)
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_4)
    }
    jvmToolchain(21)

    androidTarget()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "SharedUI"
            isStatic = true
            freeCompilerArgs += listOf(
                "-Xoverride-konan-properties=disablePhases=CastsOptimization"
            )
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":domain"))
            api(project(":data"))
            implementation(libs.compose.multiplatform.runtime)
            implementation(libs.compose.multiplatform.foundation)
            implementation(libs.compose.multiplatform.material)
            implementation(libs.compose.multiplatform.ui)
            implementation(libs.compose.multiplatform.components.resources)
            implementation(libs.compottie)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.androidx.lifecycle.viewmodel.kmp)
            implementation(libs.kotlin.inject.runtime)
        }
    }
}

dependencies {
    add("kspAndroid", libs.kotlin.inject.compiler.ksp)
    add("kspIosX64", libs.kotlin.inject.compiler.ksp)
    add("kspIosArm64", libs.kotlin.inject.compiler.ksp)
    add("kspIosSimulatorArm64", libs.kotlin.inject.compiler.ksp)
}
