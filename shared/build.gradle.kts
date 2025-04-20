plugins {
    id("com.android.library")         // Android 라이브러리 플러그인
    kotlin("android")                // Kotlin Android
    kotlin("kapt")                   // Annotation Processor (KAPT)
}
kotlin {
    compilerOptions {
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
    }
    kotlin {
        jvmToolchain(21)
    }
}
android {
    namespace = "zero.friends.gostopcalculator"
    compileSdk = Versions.compileSdk
    kotlinOptions {
        jvmTarget = "21"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation(Dep.inject)
}
