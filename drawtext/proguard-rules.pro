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

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*


-keepattributes *Annotation*
-keepattributes InnerClasses,Signature
-keepattributes SourceFile,LineNumberTable



-dontwarn junit.**
-keep class junit.* { *; }



-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context);
}





-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-keep class org.aspectj.** {*;} # 不用管爆红

-keep class com.demo.drawtext.aop.* {*;}

## 不混淆使用了注解的类及类成员
-keep @com.demo.drawtext.aop.CheckLogin class * {*;}
## 如果类中有使用了注解的方法，则不混淆类和类成员
-keepclasseswithmembers class * {
    @com.demo.drawtext.aop.CheckLogin <methods>; #必须的
}

## 不混淆使用了注解的类及类成员
-keep @org.aspectj.lang.annotation.Aspect class * {*;}
## 如果类中有使用了注解的字段，则不混淆类和类成员
#-keepclasseswithmembers class * {
#    @com.demo.drawtext.aop.CheckLogin <fields>;
#}
## 如果类中有使用了注解的构造函数，则不混淆类和类成员
#-keepclasseswithmembers class * {
#    @com.demo.drawtext.aop.CheckLogin <init>(...);
#}



