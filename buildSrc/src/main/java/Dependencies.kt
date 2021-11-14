object Versions {
    const val compileSdk = 31
    const val buildTools = "30.0.3"

    const val minSdk = 21
    const val targetSdk = 30
    const val versionCode = 1
    const val versionName = "0.0.0"
}

object Dependencies {
    const val androidGradlePlugin = "com.android.tools.build:gradle:7.0.2"

    object AndroidX {
        const val core = "androidx.core:core-ktx:1.6.0"

        object Activity {
            private const val version = "1.4.0-alpha02"
            const val activity = "androidx.activity:activity-ktx:$version"
            const val compose = "androidx.activity:activity-compose:$version"
        }

        const val fragment = "androidx.fragment:fragment-ktx:1.3.6"
        const val material = "com.google.android.material:material:1.4.0"
        const val constraintCompose = "androidx.constraintlayout:constraintlayout-compose:1.0.0-beta02"

        object Lifecycle {
            private const val lifecycleVersion = "2.3.1"
            const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion"
            const val composeViewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07"
        }

        object Room {
            private const val room_version = "2.4.0-alpha04"
            const val room = "androidx.room:room-runtime:$room_version"
            const val roomKtx = "androidx.room:room-ktx:$room_version"
            const val roomCompiler = "androidx.room:room-compiler:$room_version"
        }

    }

    object Navigation {
        private const val version = "2.4.0-beta01"
        const val fragment = "androidx.navigation:navigation-fragment-ktx:$version"
        const val ui = "androidx.navigation:navigation-ui-ktx:$version"
        const val compose = "androidx.navigation:navigation-compose:$version"
    }

    object Compose {
        const val version = "1.0.4"
        const val ui = "androidx.compose.ui:ui:$version"
        const val material = "androidx.compose.material:material:$version"
        const val tooling = "androidx.compose.ui:ui-tooling:$version"
        const val themeAdapter = "com.google.android.material:compose-theme-adapter:$version"
    }

    object Kotlin {
        private const val version = "1.5.31"
        private const val coroutineVersion = "1.5.1"

        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"

        const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0"
        const val serializationPlugin = "org.jetbrains.kotlin:kotlin-serialization:$version"

        const val coroutineCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion"
        const val coroutineAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion"

    }

    object Dagger {
        private const val daggerVersion = "2.40.1"
        const val hiltAndroid = "com.google.dagger:hilt-android:$daggerVersion"
        const val hiltCompiler = "com.google.dagger:hilt-compiler:$daggerVersion"
        const val hiltGradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:$daggerVersion"
        const val hiltLifeCycleViewModel = "androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03"
        const val navigationCompose = "androidx.hilt:hilt-navigation-compose:1.0.0-alpha03"
    }

    object Square {
        const val retrofit = "com.squareup.retrofit2:retrofit:2.9.0"
        const val okhttp3_logging = "com.squareup.okhttp3:logging-interceptor:4.9.1"
        const val serialization = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0"
    }

    const val timber = "com.jakewharton.timber:timber:4.7.1"
    const val inject = "javax.inject:javax.inject:1"
}