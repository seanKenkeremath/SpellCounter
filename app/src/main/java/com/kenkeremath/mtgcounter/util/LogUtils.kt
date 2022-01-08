package com.kenkeremath.mtgcounter.util

import android.util.Log
import com.kenkeremath.mtgcounter.BuildConfig

object LogUtils {

    const val TAG_INCREMENTER = "SC_Incrementer_Debug"
    const val TAG_TABLETOP_TOUCH_EVENTS = "SC_Tabletop_TE_Debug"
    const val TAG_PULL_TO_REVEAL = "SC_PTR_Debug"

    fun d(message: String, tag: String = "SpellCounterDebug") {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }
}