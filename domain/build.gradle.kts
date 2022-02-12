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
    implementation(Dep.inject)
    implementation(Dep.Kotlin.coroutineCore)

    implementation(Dep.Kotlin.serialization)
    implementation(Dep.Square.serialization)
    implementation(Dep.AndroidX.annotation)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}