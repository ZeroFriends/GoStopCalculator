plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("kotlinx-serialization")
}

android {
    compileSdk = Versions.compileSdk
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":shared"))
    implementation(Dep.Dagger.hiltAndroid)
    kapt(Dep.Dagger.hiltCompiler)

    implementation(Dep.Kotlin.coroutineCore)
    implementation(Dep.Kotlin.serialization)

    api(Dep.AndroidX.Room.room)
    implementation(Dep.AndroidX.Room.roomKtx)
    kapt(Dep.AndroidX.Room.roomCompiler)

    api(Dep.Square.retrofit)
    implementation(Dep.Square.okhttp3_logging)
    implementation(Dep.Square.serialization)

    implementation(Dep.timber)

}