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

-dontwarn com.imagealgorithmlab.**
-dontwarn com.hsm.**
-dontwarn com.rscja.deviceapi.**
-keep class com.hsm.** {*; }
-keep class com.rscja.deviceapi.** {*; }
-keep interface com.rscja.ht.ui.JavascriptInterface {*;}
-keep class * implements com.rscja.ht.ui.JavascriptInterface {*;}

# 蓝牙固件升级
-keep class no.nordicsemi.android.dfu.** { *; }