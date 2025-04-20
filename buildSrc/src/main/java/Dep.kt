object Versions {
    const val compileSdk = 35
    const val buildTools = "30.0.3"

    const val minSdk = 26
    const val targetSdk = 34
    const val versionCode = 11
    const val versionName = "1.0.10"
}

object Dep {
    const val androidGradlePlugin = "com.android.tools.build:gradle:8.8.2"

    object AndroidX {
        const val core = "androidx.core:core-ktx:1.16.0"
        const val startUp = "androidx.startup:startup-runtime:1.2.0"
        object Activity {
            const val activity = "androidx.activity:activity-ktx:1.10.1"
            const val compose = "androidx.activity:activity-compose:1.10.1"
        }

        const val fragment = "androidx.fragment:fragment-ktx:1.8.6"
        const val material = "com.google.android.material:material:1.12.0"
        const val constraintCompose = "androidx.constraintlayout:constraintlayout-compose:1.1.0"

        object Lifecycle {
            private const val version = "2.8.7"
            const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
            const val composeViewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:$version"
        }

        object Room {
            private const val version = "2.7.0"
            const val room = "androidx.room:room-runtime:$version"
            const val roomKtx = "androidx.room:room-ktx:$version"
            const val roomCompiler = "androidx.room:room-compiler:$version"
        }

    }

    object Navigation {
        private const val version = "2.8.9"
        const val fragment = "androidx.navigation:navigation-fragment-ktx:$version"
        const val ui = "androidx.navigation:navigation-ui-ktx:$version"
        const val compose = "androidx.navigation:navigation-compose:$version"
    }

    object Compose {
        const val composeVersion = "1.7.8"
        const val ui = "androidx.compose.ui:ui:$composeVersion"
        const val material = "androidx.compose.material:material:$composeVersion"
        const val tooling = "androidx.compose.ui:ui-tooling:$composeVersion"
        const val themeAdapter = "com.google.android.material:compose-theme-adapter:1.2.1"
    }

    object Accompanist {
        const val systemController = "com.google.accompanist:accompanist-systemuicontroller:0.22.0-rc"
        const val pager = "com.google.accompanist:accompanist-pager:0.22.0-rc"
        const val pagerIndicator = "com.google.accompanist:accompanist-pager-indicators:0.22.0-rc"
    }

    object Kotlin {
        private const val version = "2.1.20"
        private const val coroutineVersion = "1.10.2"

        const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0"
        const val serializationPlugin = "org.jetbrains.kotlin:kotlin-serialization:$version"

        const val coroutineCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion"
        const val coroutineAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion"

        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:1.6.0"

    }

    object Dagger {
        private const val versuion = "2.56.1"
        const val hiltAndroid = "com.google.dagger:hilt-android:$versuion"
        const val hiltCompiler = "com.google.dagger:hilt-compiler:$versuion"
        const val hiltGradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:$versuion"
        const val navigationCompose = "androidx.hilt:hilt-navigation-compose:1.2.0"
    }

    object Square {
        const val retrofit = "com.squareup.retrofit2:retrofit:2.9.0"
        const val okhttp3_logging = "com.squareup.okhttp3:logging-interceptor:4.9.1"
        const val serialization = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0"
    }

    object Google {
        const val googleService = "com.google.gms:google-services:4.3.10"
        const val crashlytics = "com.google.firebase:firebase-crashlytics-gradle:2.8.1"

        const val firebase = "com.google.firebase:firebase-bom:29.0.4"

        const val firebaseAnalyticsKtx = "com.google.firebase:firebase-analytics-ktx"
        const val firebaseAnalytics = "com.google.firebase:firebase-analytics:20.0.2"
        const val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics-ktx"

    }

    const val lottie = "com.airbnb.android:lottie-compose:4.2.2"
    const val timber = "com.jakewharton.timber:timber:4.7.1"
    const val inject = "javax.inject:javax.inject:1"
    const val admob = "com.google.android.gms:play-services-ads:20.5.0"
}
