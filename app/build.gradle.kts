plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    id("kotlin-parcelize")
}

android {
    namespace = "zero.friends.gostopcalculator"
    compileSdk = libs.versions.compileSdk.get().toInt()

    sourceSets {
        getByName("main") {
            manifest.srcFile("src/main/AndroidManifest.xml")
            java.srcDirs(
                "src/main/java",
                "src/androidMain/kotlin",
                "src/commonMain/kotlin"
            )
            res.srcDirs("src/main/res")
        }
    }

    defaultConfig {
        applicationId = "zero.friends.gostopcalculator"
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
        minSdk = libs.versions.minSdk.get().toInt()
    }

    signingConfigs {
        create("release") {
            keyAlias = "key"
            keyPassword = "zerofriends"
 storeFile = file("keystore/keystore.jks")
            storePassword = "zerofriends"
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            buildConfigField("String", "AD_UNIT_ID", "\"ca-app-pub-3940256099942544/1033173712\"")
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
            buildConfigField("String", "AD_UNIT_ID", "\"ca-app-pub-4169469417741632/1919936924\"")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
    }

    lint {
        abortOnError = false
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
        unitTests.all {
            it.filter {
                isFailOnNoMatchingTests = false
            }
        }
    }
}

dependencies {
    implementation(project(":shared"))

    implementation(libs.compose.multiplatform.runtime)
    implementation(libs.compose.multiplatform.foundation)
    implementation(libs.compose.multiplatform.material)
    implementation(libs.compose.multiplatform.ui)
    implementation(libs.compose.multiplatform.components.resources)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.sqlite.bundled)

    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.serialization.json)

    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.androidx.material)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.navigation.compose)
    implementation(libs.androidx.room.ktx)
    implementation(libs.lottie.compose)
    implementation(libs.timber)
    implementation(libs.kotlin.inject.runtime)
    implementation(project.dependencies.platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.play.services.ads)

    ksp(libs.kotlin.inject.compiler.ksp)
    testImplementation(libs.junit)
}
