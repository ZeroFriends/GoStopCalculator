object Versions {
    const val compileSdk = 31
    const val buildTools = "30.0.3"

    const val minSdk = 24
    const val targetSdk = 32
    const val versionCode = 3
    const val versionName = "1.0.2"
}

object Dep {
    const val androidGradlePlugin = "com.android.tools.build:gradle:7.0.2"

    object AndroidX {
        const val core = "androidx.core:core-ktx:1.7.0"
        const val startUp = "androidx.startup:startup-runtime:1.1.0"

        object Activity {
            const val activity = "androidx.activity:activity-ktx:1.4.0"
            const val compose = "androidx.activity:activity-compose:1.4.0"
        }

        const val fragment = "androidx.fragment:fragment-ktx:1.4.1"
        const val material = "com.google.android.material:material:1.5.0"
        const val constraintCompose = "androidx.constraintlayout:constraintlayout-compose:1.0.0"

        object Lifecycle {
            const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0"
            const val composeViewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07"
        }

        object Room {
            const val room = "androidx.room:room-runtime:2.4.1"
            const val roomKtx = "androidx.room:room-ktx:2.4.1"
            const val roomCompiler = "androidx.room:room-compiler:2.4.1"
        }

    }

    object Navigation {
        const val fragment = "androidx.navigation:navigation-fragment-ktx:2.4.0"
        const val ui = "androidx.navigation:navigation-ui-ktx:2.4.0"
        const val compose = "androidx.navigation:navigation-compose:2.4.0"
    }

    object Compose {
        const val composeVersion = "1.0.5"
        const val ui = "androidx.compose.ui:ui:$composeVersion"
        const val material = "androidx.compose.material:material:$composeVersion"
        const val tooling = "androidx.compose.ui:ui-tooling:$composeVersion"
        const val themeAdapter = "com.google.android.material:compose-theme-adapter:$composeVersion"
    }

    object Accompanist {
        const val systemController = "com.google.accompanist:accompanist-systemuicontroller:0.22.0-rc"
        const val pager = "com.google.accompanist:accompanist-pager:0.22.0-rc"
        const val pagerIndicator = "com.google.accompanist:accompanist-pager-indicators:0.22.0-rc"
    }

    object Kotlin {
        private const val version = "1.5.31"
        private const val coroutineVersion = "1.5.1"

        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"

        const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0"
        const val serializationPlugin = "org.jetbrains.kotlin:kotlin-serialization:$version"

        const val coroutineCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion"
        const val coroutineAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion"

        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:1.6.0"

    }

    object Dagger {
        const val hiltAndroid = "com.google.dagger:hilt-android:2.40.1"
        const val hiltCompiler = "com.google.dagger:hilt-compiler:2.40.1"
        const val hiltGradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:2.40.1"
        const val hiltLifeCycleViewModel = "androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03"
        const val navigationCompose = "androidx.hilt:hilt-navigation-compose:1.0.0"
    }

    object Square {
        const val retrofit = "com.squareup.retrofit2:retrofit:2.9.0"
        const val okhttp3_logging = "com.squareup.okhttp3:logging-interceptor:4.9.1"
        const val serialization = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0"
    }

    const val lottie = "com.airbnb.android:lottie-compose:4.2.2"
    const val timber = "com.jakewharton.timber:timber:4.7.1"
    const val inject = "javax.inject:javax.inject:1"
    const val admob = "com.google.android.gms:play-services-ads:20.5.0"
}