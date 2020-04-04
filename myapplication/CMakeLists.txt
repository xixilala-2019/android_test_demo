cmake_minimum_required(VERSION 3.4.1)

add_library( # 文件名
             mp3

             # 将库设置为共享库
             SHARED

             # 源文件的相对路径
             src/main/cpp/mp3.cpp )

include_directories(src/main/cpp/)

find_library(log-lib
             log)

target_link_libraries(mp3
                      ${log-lib})