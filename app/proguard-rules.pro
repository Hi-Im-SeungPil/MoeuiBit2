# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.slf4j.impl.StaticLoggerBinder

-dontwarn org.jetbrains.kotlin.**

-dontwarn com.google.android.gms.**
-keep class com.google.android.gms.** { *; }
-keep class com.google.firebase.** { *; }
-keep class org.jeonfeel.moeuibit2.data.** {*;}

-keepnames @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel

-keep class org.jeonfeel.moeuibit2.data.network.retrofit.api.** { *; }

# Retrofit
-keep class retrofit2.** { *; }

# OkHttp
-keep class okhttp3.** { *; }

# Gson
-keep class com.google.gson.** { *; }

-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.examples.android.model.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

 -keep,allowobfuscation,allowshrinking interface retrofit2.Call
 -keep,allowobfuscation,allowshrinking class retrofit2.Response
 -keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-keep class com.skydoves.balloon.** { *; }
-keep class ** extends com.skydoves.balloon.**
-keep class androidx.datastore.*.** {*;}
-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }
