# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * { @com.google.gson.annotations.SerializedName <fields>; }
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

# SimpleXML
-keep interface org.simpleframework.xml.core.Label { public *; }
-keep class * implements org.simpleframework.xml.core.Label { public *; }
-keep interface org.simpleframework.xml.core.Parameter { public *; }
-keep class * implements org.simpleframework.xml.core.Parameter { public *; }
-keep interface org.simpleframework.xml.core.Extractor { public *; }
-keep class * implements org.simpleframework.xml.core.Extractor { public *; }
-keepclassmembers,allowobfuscation class * { @org.simpleframework.xml.Text <fields>; }
-keepclassmembers,allowobfuscation class * { @org.simpleframework.xml.Path <fields>; }
-keepclassmembers,allowobfuscation class * { @org.simpleframework.xml.ElementList <fields>; }

# bean
-keep public class com.lvdoui6.android.tv.lvdou.bean.Adm { *; }
-keep public class com.lvdoui6.android.tv.lvdou.bean.Adm$DataBean { *; }
-keep public class com.lvdoui6.android.tv.lvdou.bean.Adm$DataBean$* { *; }
-keep public class com.lvdoui6.android.tv.lvdou.bean.Adm$DataBean$NoticeListBean$* { *; }
-keep public class com.lvdoui6.android.tv.lvdou.bean.AdmUser { *; }
-keep public class com.lvdoui6.android.tv.lvdou.bean.AdmUser$DataBean { *; }
-keep public class com.lvdoui6.android.tv.lvdou.bean.AdmUser$DataBean$UserinfoBean { *; }
-keep public class com.lvdoui6.android.tv.lvdou.bean.AdmUser$DataBean$* { *; }
-keep public class com.lvdoui6.android.tv.lvdou.bean.AdmGroup { *; }
-keep public class com.lvdoui6.android.tv.lvdou.bean.AdmGroup$DataBean { *; }
-keep public class com.lvdoui6.android.tv.lvdou.bean.AdmGroup$DataBean$* { *; }
-keep public class com.lvdoui6.android.tv.lvdou.bean.QWeather { *; }
-keep public class com.lvdoui6.android.tv.lvdou.bean.QWeather$DailyBean { *; }

# OkHttp
-dontwarn okhttp3.**
-keep class okio.** { *; }
-keep class okhttp3.** { *; }

# CatVod
-keep class com.github.catvod.crawler.** { *; }
-keep class * extends com.github.catvod.crawler.Spider

# Cling
-keep class org.fourthline.cling.** { *; }
-keep class javax.xml.** { *; }

# Cronet
-keep class org.chromium.net.** { *; }
-keep class com.google.net.cronet.** { *; }

# EXO
-keep class org.xmlpull.v1.** { *; }

# IJK
-keep class tv.danmaku.ijk.media.player.** { *; }

# Jianpian
-keep class com.p2p.** { *; }

# Mozilla
-keep class org.mozilla.javascript.** { *; }

# Nano
-keep class fi.iki.elonen.** { *; }

# QuickJS
-keep class com.whl.quickjs.** { *; }

# Sardine
-keep class com.thegrizzlylabs.sardineandroid.** { *; }

# Smbj
-keep class com.hierynomus.** { *; }
-keep class net.engio.mbassy.** { *; }

# TVBus
-keep class com.tvbus.engine.** { *; }

# XunLei
-keep class com.xunlei.downloadlib.** { *; }

# ZLive
-keep class com.sun.jna.** { *; }
-keep class com.east.android.zlive.** { *; }

# Zxing
-keep class com.google.zxing.** { *; }
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# x5
-dontwarn dalvik.**
-dontwarn com.tencent.smtt.**
-keep class com.tencent.smtt.** { *; }
-keep class com.tencent.tbs.** { *; }

# baidu
-keep class com.baidu.mobstat.** { *; }

# mqtt
-keep class org.eclipse.paho.client.mqttv3.** { *; }

# marqueeview
-keep class com.sunfusheng.marqueeview.** { *; }

# androidsvg
-keep class com.caverock.androidsvg.** { *; }