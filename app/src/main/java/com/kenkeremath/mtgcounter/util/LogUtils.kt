package com.kenkeremath.mtgcounter.util

import android.util.Log
import com.kenkeremath.mtgcounter.BuildConfig

object LogUtils {
    fun d(message: String, tag: String = "SpellCounterDebug") {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }
}