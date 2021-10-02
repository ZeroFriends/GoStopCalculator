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
        const val appcompat = "androidx.appcompat:appcompat:1.3.1"

        object Activity {
            private const val version = "1.4.0-alpha02"
            const val activity = "androidx.activity:activity-ktx:$version"
            const val compose = "androidx.activity:activity-compose:$version"
        }

        const val fragment = "androidx.fragment:fragment-ktx:1.3.6"
        const val material = "com.google.android.material:material:1.4.0"
        const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.0.4"

        object Lifecycle {
            private const val lifecycleVersion = "2.3.1"
            const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion"
        }

        object Room {
            private const val room_version = "2.4.0-alpha04"
            const val room = "androidx.room:room-runtime:$room_version"
            const val roomKtx = "androidx.room:room-ktx:$room_version"
            const val roomCompiler = "androidx.room:room-compiler:$room_version"
        }

    }

    object Navigation {
        private const val version = "2.4.0-alpha08"
        const val fragment = "androidx.navigation:navigation-fragment-ktx:$version"
        const val ui = "androidx.navigation:navigation-ui-ktx:$version"
        const val compose = "androidx.navigation:navigation-compose:$version"
    }

    object Compose {
        const val version = "1.0.1"
        const val ui = "androidx.compose.ui:ui:$version"
        const val material = "androidx.compose.material:material:$version"
        const val tooling = "androidx.compose.ui:ui-tooling:$version"
        const val themeAdapter = "com.google.android.material:compose-theme-adapter:$version"
    }

    object Kotlin {
        const val version = "1.5.21"
        const val coroutineVersion = "1.5.1"

        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"

        const val coroutineCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion"
        const val coroutineAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion"

    }

    object Dagger {
        private const val daggerVersion = "2.38.1"
        const val hiltAndroid = "com.google.dagger:hilt-android:$daggerVersion"
        const val hiltCompiler = "com.google.dagger:hilt-compiler:$daggerVersion"
        const val hiltGradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:$daggerVersion"
        const val hiltLifeCycleViewModel = "androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03"
    }

    object Square {
        const val retrofit = "com.squareup.retrofit2:retrofit:2.9.0"
    }

    object Test {
        const val junit = "junit:junit:4.13.2"
        const val espresso = "androidx.test.espresso:espresso-core:3.4.0"
    }

    object Image {
        const val glide = "com.github.bumptech.glide:glide:4.12.0"
        const val glideCompiler = "com.github.bumptech.glide:compiler:4.12.0"
    }

    const val timber = "com.jakewharton.timber:timber:4.7.1"
    const val inject = "javax.inject:javax.inject:1"
}