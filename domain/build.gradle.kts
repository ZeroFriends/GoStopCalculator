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
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
