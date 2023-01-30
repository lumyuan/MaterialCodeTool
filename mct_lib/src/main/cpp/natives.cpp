#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_ly_android_material_code_natives_MaterialCodeToolNative_getKey(JNIEnv *env, jobject /*thiz*/) {
    std::string key = "1";
    return env->NewStringUTF(key.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ly_android_material_code_natives_MaterialCodeToolNative_getIvKey(JNIEnv *env, jobject /*thiz*/) {
    std::string iv = "1";
    return env->NewStringUTF(iv.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ly_android_material_code_natives_MaterialCodeToolNative_getIp(JNIEnv *env, jobject thiz) {
    std::string ip = "https";
    return env->NewStringUTF(ip.c_str());
}