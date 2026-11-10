# ==============================================================================
# General ProGuard / R8 Configuration
# ==============================================================================
# Crashlytics 역난독화 및 제네릭 타입/어노테이션 리플렉션 안전성 보존
-keepattributes *Annotation*, InnerClasses, EnclosingMethod, Signature, Exceptions
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile

# ViewModelProvider의 kClass.qualifiedName 조회 시 익명/로컬 클래스 오인 방지
-keep class kotlin.Metadata { *; }

# ==============================================================================
# Kotlin & Coroutines
# ==============================================================================
-keepclassmembers class **$WhenMappings {
    <fields>;
}
# Coroutines ServiceLoader 및 AtomicFieldUpdater 리플렉션 접근 보존
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory { *; }
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler { *; }
-keepclassmembernames class kotlinx.coroutines.** {
    volatile <fields>;
}

# ==============================================================================
# Kotlinx Serialization
# ==============================================================================
-dontnote kotlinx.serialization.SerializationKt

# @Serializable 모델 클래스 보존
-keep @kotlinx.serialization.Serializable class * {
    *;
}

# Navigation Compose의 data object 및 일반 클래스의 serializer() 메서드 보존
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Companion 객체 필드 및 serializer() 보존
-keepclassmembers class * {
    *** Companion;
}
-keepclassmembers class **$Companion {
    public kotlinx.serialization.KSerializer serializer(...);
}

# KSerializer 구현체 및 컴파일러 생성 $$serializer 인스턴스 보존
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
# AndroidX Room & SQLite (KMP)
# ==============================================================================
# Room KMP C-SQLite JNI 라이브러리(libsqliteJni.so) 통신용 BundledSQLiteDriver 보존
-keep class androidx.sqlite.driver.bundled.** { *; }

# RoomDatabase 및 Room Entity, DAO 보존
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

# TypeConverters 메서드 및 클래스 보존
-keepclassmembers class * {
    @androidx.room.TypeConverter <methods>;
}
-keep class zero.friends.data.util.SerializationConverters { *; }

# Room KMP @ConstructedBy 대상 싱글톤 INSTANCE 필드 및 생성자 보존
-keep class * implements androidx.room.RoomDatabaseConstructor {
    *;
}

# Room KSP 생성 DB/DAO 구현체 보존 (Repository 구현체 오염 방지)
-keep class zero.friends.data.source.**_Impl {
    <init>(...);
    public <methods>;
}
-dontwarn androidx.room.paging.**

# ==============================================================================
# kotlin-inject (컴파일 타임 DI)
# ==============================================================================
# 컴포넌트 인터페이스 및 KSP 생성 구현체 보존
-keep @me.tatarka.inject.annotations.Component class * {
    *;
}
-keep @me.tatarka.inject.annotations.Component interface * {
    *;
}
-keep class zero.friends.**.Inject* {
    *;
}
-keepclasseswithmembers class * {
    @me.tatarka.inject.annotations.Provides <methods>;
}

# ==============================================================================
# Compose Multiplatform Resources
# ==============================================================================
-keep class org.jetbrains.compose.resources.** { *; }
-keep class zero.friends.gostopcalculator.shared.generated.resources.** { *; }

# ==============================================================================
# Compottie & Keight Engine (Lottie)
# ==============================================================================
-dontwarn io.github.alexzhirkevich.**
-keep class io.github.alexzhirkevich.** { *; }

# ==============================================================================
# Google Mobile Ads (AdMob)
# ==============================================================================
-keep public class com.google.android.gms.ads.** {
    public *;
}
-keep public class com.google.ads.** {
    public *;
}

# ==============================================================================
# Firebase & Crashlytics
# ==============================================================================
# AdException 등 Throwable 직접 상속 클래스 포함 Crashlytics 식별 보존
-keep public class * extends java.lang.Throwable
-keepclassmembers class * {
    @com.google.firebase.crashlytics.** *;
}
-keepclassmembers class * {
    @com.google.android.gms.common.annotation.KeepName *;
}
-keepnames class * implements java.io.Serializable

# ==============================================================================
# Enum 역직렬화 보존 (findOptional Silent Data Loss 방지)
# ==============================================================================
-keepclassmembers enum zero.friends.domain.model.*Option {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers enum zero.friends.domain.util.Const$Rule {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers enum zero.friends.gostopcalculator.ui.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ==============================================================================
# Navigation Route 인터페이스 계층 보존
# ==============================================================================
-keep interface zero.friends.gostopcalculator.navigation.Route* { *; }

# ==============================================================================
# Timber 로깅 최적화
# ==============================================================================
-keep class timber.log.** { *; }
# Release 빌드에서 디버그/정보 로그 호출 바이트코드 및 문자열 인자 스트리핑
-assumenosideeffects class timber.log.Timber {
    public static void d(...);
    public static void v(...);
    public static void i(...);
}
