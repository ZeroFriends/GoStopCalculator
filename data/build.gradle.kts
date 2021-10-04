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
    api(Dependencies.AndroidX.Room.room)
    implementation(Dependencies.AndroidX.Room.roomKtx)
    kapt(Dependencies.AndroidX.Room.roomCompiler)

}