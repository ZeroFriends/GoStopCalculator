# ==============================================================================
# General ProGuard / R8 Configuration
# ==============================================================================
-keepattributes *Annotation*, InnerClasses, EnclosingMethod, Signature, Exceptions
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile

# ==============================================================================
# Kotlin & Coroutines
# ==============================================================================
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory { *; }
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler { *; }
-keepclassmembernames class kotlinx.coroutines.** {
    volatile <fields>;
}

# ==============================================================================
# Kotlinx Serialization
# ==============================================================================
-dontnote kotlinx.serialization.SerializationKt

# Keep classes annotated with @Serializable and their members
-keep @kotlinx.serialization.Serializable class * {
    *;
}

# Keep companion objects of @Serializable classes and their serializer methods
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
    @kotlinx.serialization.Serializable <methods>;
}

-keepclassmembers class **$Companion {
    public kotlinx.serialization.KSerializer serializer(...);
}

# Keep generated serializer classes and instance
-keep class * implements kotlinx.serialization.KSerializer {
    <init>(...);
    public static *** Companion;
    public static *** INSTANCE;
}
-keep class **$$serializer {
    *;
}
-keepclassmembers class **$$serializer {
    public static **$$serializer INSTANCE;
}

# ==============================================================================
# AndroidX Room
# ==============================================================================
-keep class * extends androidx.room.RoomDatabase {
    <init>(...);
}
-keep @androidx.room.Entity class * {
    <init>(...);
    <fields>;
    <methods>;
}
-keep @androidx.room.Dao interface * {
    <methods>;
}
-keep class * implements androidx.room.RoomDatabase$Callback
-keep class * extends androidx.room.migration.Migration
-keepclassmembers class * {
    @androidx.room.TypeConverter <methods>;
}
-keep class **.*_Impl {
    <init>(...);
    public <methods>;
}
-keep class **.*Constructor {
    <init>(...);
    public <methods>;
}
-dontwarn androidx.room.paging.**

# ==============================================================================
# kotlin-inject
# ==============================================================================
-keep @me.tatarka.inject.annotations.Component class * {
    *;
}
-keep @me.tatarka.inject.annotations.Component interface * {
    *;
}
-keep class **.Inject* {
    *;
}
-keep class **.*ComponentImpl {
    *;
}
-keepclasseswithmembers class * {
    @me.tatarka.inject.annotations.Inject <init>(...);
}
-keepclasseswithmembers class * {
    @me.tatarka.inject.annotations.Provides <methods>;
}

# ==============================================================================
# Compose Multiplatform & Resources
# ==============================================================================
-keep class androidx.compose.ui.platform.** { *; }
-keep class androidx.compose.runtime.** { *; }
-keep class org.jetbrains.compose.resources.** { *; }
-keep class zero.friends.gostopcalculator.shared.generated.resources.** { *; }

# ==============================================================================
# Compottie & Lottie
# ==============================================================================
-dontwarn io.github.alexzhirkevich.compottie.**
-keep class io.github.alexzhirkevich.compottie.** { *; }
-dontwarn com.airbnb.lottie.**
-keep class com.airbnb.lottie.** { *; }

# ==============================================================================
# Google Mobile Ads (AdMob) & Play Services
# ==============================================================================
-keep public class com.google.android.gms.ads.** {
    public *;
}
-keep public class com.google.ads.** {
    public *;
}
-keep class com.google.android.gms.internal.ads.** { *; }
-dontwarn com.google.android.gms.ads.**

# ==============================================================================
# Firebase & Crashlytics
# ==============================================================================
-keep public class * extends java.lang.Exception
-keepclassmembers class * {
    @com.google.firebase.crashlytics.** *;
}
-keepclassmembers class * {
    @com.google.android.gms.common.annotation.KeepName *;
}
-keepnames class * implements java.io.Serializable
-dontwarn com.google.firebase.**

# ==============================================================================
# Timber
# ==============================================================================
-dontwarn timber.log.**
-keep class timber.log.** { *; }

# ==============================================================================
# Domain & Data Models
# ==============================================================================
-keep class zero.friends.domain.model.** { *; }
-keep class zero.friends.data.entity.** { *; }
-keep class zero.friends.domain.util.Const** { *; }
