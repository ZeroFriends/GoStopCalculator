plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = Versions.compileSdk

    defaultConfig {
        applicationId = "zero.friends.gostopcalculator"
        targetSdk = Versions.targetSdk
        versionCode = Versions.versionCode
        versionName = Versions.versionName
        minSdk = Versions.minSdk
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
//    implementation(project(":domain"))
//    implementation(project(":data"))

    implementation(Dependencies.Compose.ui)
    implementation(Dependencies.Compose.tooling)
    implementation(Dependencies.Compose.material)
    implementation(Dependencies.Compose.themeAdapter)


    implementation(Dependencies.AndroidX.core)
    implementation(Dependencies.AndroidX.appcompat)
    implementation(Dependencies.AndroidX.material)
    implementation(Dependencies.AndroidX.constraintlayout)
    implementation(Dependencies.AndroidX.fragment)
    implementation(Dependencies.AndroidX.Activity.activity)
    implementation(Dependencies.AndroidX.Lifecycle.viewModel)

    androidTestImplementation(Dependencies.Test.junit)
    androidTestImplementation(Dependencies.Test.espresso)

    implementation(Dependencies.Kotlin.coroutineCore)
    implementation(Dependencies.Kotlin.coroutineAndroid)

    implementation(Dependencies.Dagger.hiltAndroid)
    implementation(Dependencies.Dagger.hiltLifeCycleViewModel)
    kapt(Dependencies.Dagger.hiltCompiler)

    implementation(Dependencies.Image.glide)
    implementation(Dependencies.Image.glideCompiler)
}