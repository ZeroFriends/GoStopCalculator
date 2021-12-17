plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdk = Versions.compileSdk
}

dependencies {
    implementation(Dep.inject)
}