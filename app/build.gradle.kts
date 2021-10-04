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
        kotlinCompilerExtensionVersion = Dependencies.Compose.version
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(Dependencies.Compose.ui)
    implementation(Dependencies.Compose.tooling)
    implementation(Dependencies.Compose.material)
    implementation(Dependencies.Compose.themeAdapter)


    implementation(Dependencies.AndroidX.core)
    implementation(Dependencies.AndroidX.fragment)
    implementation(Dependencies.AndroidX.constraintCompose)
    implementation(Dependencies.AndroidX.Activity.activity)
    implementation(Dependencies.AndroidX.Activity.compose)
    implementation(Dependencies.AndroidX.Lifecycle.viewModel)
    implementation(Dependencies.AndroidX.Lifecycle.composeViewModel)

    implementation(Dependencies.Navigation.ui)
    implementation(Dependencies.Navigation.fragment)
    implementation(Dependencies.Navigation.compose)

    androidTestImplementation(Dependencies.Test.junit)
    androidTestImplementation(Dependencies.Test.espresso)

    implementation(Dependencies.Kotlin.coroutineCore)
    implementation(Dependencies.Kotlin.coroutineAndroid)

    implementation(Dependencies.Dagger.hiltAndroid)
    implementation(Dependencies.Dagger.hiltLifeCycleViewModel)
    implementation(Dependencies.Dagger.navigationCompose)
    kapt(Dependencies.Dagger.hiltCompiler)

    implementation(Dependencies.timber)
}