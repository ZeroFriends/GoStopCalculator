// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Dep.androidGradlePlugin)
        classpath(Dep.Dagger.hiltGradlePlugin)
        classpath(Dep.Kotlin.serializationPlugin)
        classpath(Dep.Google.googleService)
        classpath(Dep.Google.crashlytics)
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
plugins {
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20" // this version matches your Kotlin version
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }

}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
