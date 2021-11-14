plugins {
    id("kotlin")
    kotlin("kapt")
    id("java-library")
    id("kotlinx-serialization")
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
dependencies {
    implementation(Dependencies.inject)
    implementation(Dependencies.Kotlin.coroutineCore)

    implementation(Dependencies.Kotlin.serialization)
    implementation(Dependencies.Square.serialization)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}