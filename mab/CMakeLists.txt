cmake_minimum_required(VERSION 3.4.1)

#if (${ANDROID_ABI} STREQUAL "armeabi-v7a")
#    include_directories(${ANDROID_SYSROOT}/usr/include/arm-linux-androideabi)
#    set(CMAKE_C_FLAGS "-DHAVE_CONFIG_H -DFPM_ARM -ffast-math -O3")
#endif()

add_library(mad
            SHARED
#            src/main/cpp/mad/version.c
#            src/main/cpp/mad/fixed.c
#            src/main/cpp/mad/bit.c
#            src/main/cpp/mad/timer.c
#            src/main/cpp/mad/stream.c
#            src/main/cpp/mad/frame.c
#            src/main/cpp/mad/synth.c
#            src/main/cpp/mad/decoder.c
#            src/main/cpp/mad/layer12.c
#            src/main/cpp/mad/layer3.c
#            src/main/cpp/mad/huffman.c
#            src/main/cpp/jni_mad_mp3_wrap.cpp
#            src/main/cpp/NativeFileOperator.c
#            src/main/cpp/NativeMP3Decoder.c


            )

include_directories(src/main/cpp/)

find_library(log-lib
             log)

target_link_libraries(mad
                      log)