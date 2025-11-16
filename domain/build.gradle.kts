plugins {
    alias(libs.plugins.kotlin.jvm)
    kotlin("kapt")
    id("java-library")
    alias(libs.plugins.kotlin.serialization)
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
