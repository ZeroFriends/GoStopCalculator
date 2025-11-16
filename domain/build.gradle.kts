plugins {
    alias(libs.plugins.kotlin.jvm)
    kotlin("kapt")
    id("java-library")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    compilerOptions {
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
    }
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
dependencies {
    implementation(libs.javax.inject)
    implementation(libs.kotlin.coroutines.core)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.retrofit.kotlinx.serialization)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
