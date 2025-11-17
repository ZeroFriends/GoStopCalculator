plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
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
    testLogging {
        events("passed", "skipped", "failed")
    }
}
dependencies {
    implementation(libs.javax.inject)
    implementation(libs.kotlin.coroutines.core)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.retrofit.kotlinx.serialization)
    
    // Test
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.coroutines.test)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
