plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")

}

android {
    compileSdk = Versions.compileSdk
}

dependencies {
    implementation(project(":domain"))
    implementation(Dependencies.Dagger.hiltAndroid)
    kapt(Dependencies.Dagger.hiltCompiler)

    implementation(Dependencies.Kotlin.coroutineCore)
    implementation(Dependencies.Kotlin.serialization)

    api(Dependencies.AndroidX.Room.room)
    implementation(Dependencies.AndroidX.Room.roomKtx)
    kapt(Dependencies.AndroidX.Room.roomCompiler)

    api(Dependencies.Square.retrofit)
    implementation(Dependencies.Square.okhttp3_logging)
    implementation(Dependencies.Square.serialization)

}