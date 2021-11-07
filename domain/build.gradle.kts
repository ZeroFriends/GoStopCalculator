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

    testImplementation(Dependencies.Test.junit5)
    testImplementation(platform(Dependencies.Test.junit_bom))
    testImplementation(Dependencies.Test.mockito_kotlin)
    testImplementation(Dependencies.Test.mockito_core)
    testImplementation(Dependencies.Test.mockito_inline)

    implementation(Dependencies.Square.serialization)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}