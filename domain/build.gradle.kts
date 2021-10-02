plugins {
    id("kotlin")
    kotlin("kapt")
    id("java-library")
}

dependencies {
    project(":data")
    implementation(Dependencies.inject)
    implementation(Dependencies.Kotlin.coroutineCore)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}