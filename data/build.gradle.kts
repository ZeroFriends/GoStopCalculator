plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("kotlinx-serialization")
}

android {
    namespace = "zero.friends.gostopcalculator"
    compileSdk = Versions.compileSdk
}
kotlin {
    compilerOptions {
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
    }
    kotlin {
        jvmToolchain(21)
    }
}


dependencies {
    implementation(project(":domain"))
    implementation(project(":shared"))
    implementation(Dep.Dagger.hiltAndroid)
    kapt(Dep.Dagger.hiltCompiler)

    implementation(Dep.Kotlin.coroutineCore)
    implementation(Dep.Kotlin.serialization)

    implementation(Dep.AndroidX.Room.room)
    implementation(Dep.AndroidX.Room.roomKtx)
    kapt(Dep.AndroidX.Room.roomCompiler)

    implementation(Dep.Square.retrofit)
    implementation(Dep.Square.okhttp3_logging)
    implementation(Dep.Square.serialization)

    implementation(Dep.timber)

}
