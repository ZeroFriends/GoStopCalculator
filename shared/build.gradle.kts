plugins {
    alias(libs.plugins.android.library)         // Android 라이브러리 플러그인
    alias(libs.plugins.kotlin.android)                // Kotlin Android
    kotlin("kapt")                   // Annotation Processor (KAPT)
}
kotlin {
    compilerOptions {
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
    }
    jvmToolchain(21)
}
android {
    namespace = "zero.friends.gostopcalculator"
    compileSdk = libs.versions.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation(libs.javax.inject)
}
