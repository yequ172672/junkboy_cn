# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# TensorFlow Lite rules
-keep class org.tensorflow.lite.** { *; }
-keep class org.tensorflow.lite.support.** { *; }
-keep interface org.tensorflow.lite.** { *; }
-keep interface org.tensorflow.lite.support.** { *; }

# AutoValue classes (used by TensorFlow Lite)
-dontwarn com.google.auto.value.**
-keep class com.google.auto.value.** { *; }

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep Room database classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-dontwarn androidx.room.paging.**

# Keep database entities and DAOs
-keep class com.ovehbe.junkboy.database.** { *; }

# Keep Jetpack Compose classes
-keep class androidx.compose.** { *; }
-keep interface androidx.compose.** { *; }

# Keep SMS related classes
-keep class com.ovehbe.junkboy.smsreceiver.** { *; }
-keep class com.ovehbe.junkboy.service.** { *; }
-keep class com.ovehbe.junkboy.classifier.** { *; }
-keep class com.ovehbe.junkboy.filters.** { *; }

# Keep Gson classes for JSON serialization
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep Kotlin coroutines
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# Keep Android components
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider 