-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

-keepattributes Signature
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod

-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

-keepclassmembers class * {
    *** Companion;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

-keep class * implements kotlinx.serialization.internal.InternalSerializationApi

-keep @kotlinx.serialization.Serializable class ** {
    <init>(...);
    *** Companion;
}
-keepclassmembers class ** {
    *** Companion;
}
-keepclassmembers @kotlinx.serialization.Serializable class * {
    static *** Companion;
    *** write$Self(...);
    *** read$Self(...);
}
-if @kotlinx.serialization.Serializable class ** {
    static *** Companion;
}
-keepclassmembers class **$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**

-keep class com.qinoteapp.qinoteapp.data.entity.** { *; }
-keep class com.qinoteapp.qinoteapp.network.ChatMessage { *; }
-keep class com.qinoteapp.qinoteapp.network.AiResponse { *; }
-keep class com.qinoteapp.qinoteapp.network.ParsedBill { *; }
-keep class com.qinoteapp.qinoteapp.data.local.AiConfig { *; }
-keep class com.qinoteapp.qinoteapp.data.local.AppConfig { *; }

-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.**

-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-keepclassmembers class * {
    @javax.inject.Inject <init>(...);
}
-keepclassmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okio.**
-keep class okio.** { *; }

-keep class coil.** { *; }
-keep interface coil.** { *; }

-keep class dev.rikka.shizuku.** { *; }
-keep interface dev.rikka.shizuku.** { *; }

-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

-keep class com.xzakota.hyper.notification.** { *; }
-dontwarn com.xzakota.hyper.notification.**

-keepclassmembers class * {
    public void on*Click(...);
    public void on*Click(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
