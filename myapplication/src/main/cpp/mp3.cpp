//
 //  Created  by  xixlala  on  2019.4.23.
 //

#include <jni.h>
#include <string>

 JNIEXPORT  jstring  JNICALL
 Java_com_demo_myapplication_FaceActivity_abc(
         JNIEnv *env,
         jobject instance) {
     std::string hello = "Hello from C++";
     return env->NewStringUTF(hello.c_str());
 }