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

    kotlinOptions {
        jvmTarget = "1.8"
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

    composeOptions {
        kotlinCompilerExtensionVersion = Dep.Compose.version
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":shared"))

    implementation(Dep.Compose.ui)
    implementation(Dep.Compose.tooling)
    implementation(Dep.Compose.material)
    implementation(Dep.Compose.themeAdapter)
    implementation(Dep.lottie)

    implementation(Dep.AndroidX.core)
    implementation(Dep.AndroidX.fragment)
    implementation(Dep.AndroidX.constraintCompose)
    implementation(Dep.AndroidX.Activity.activity)
    implementation(Dep.AndroidX.Activity.compose)
    implementation(Dep.AndroidX.Lifecycle.viewModel)
    implementation(Dep.AndroidX.Lifecycle.composeViewModel)

    implementation(Dep.Navigation.ui)
    implementation(Dep.Navigation.fragment)
    implementation(Dep.Navigation.compose)

    implementation(Dep.Kotlin.coroutineCore)
    implementation(Dep.Kotlin.coroutineAndroid)

    implementation(Dep.Dagger.hiltAndroid)
    implementation(Dep.Dagger.hiltLifeCycleViewModel)
    implementation(Dep.Dagger.navigationCompose)
    kapt(Dep.Dagger.hiltCompiler)

    implementation(Dep.timber)
    implementation(Dep.Kotlin.reflect)
}