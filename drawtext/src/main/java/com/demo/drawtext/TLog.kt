package com.demo.drawtext

import com.orhanobut.logger.Logger
import java.lang.StringBuilder

class TLog {

    companion object {
        fun info(vararg data:Any) {
            val info = StringBuilder()
            for (d in data) {
                info.append(d.toString())
            }
            Logger.d(" --test-- $info" )
        }
    }
}