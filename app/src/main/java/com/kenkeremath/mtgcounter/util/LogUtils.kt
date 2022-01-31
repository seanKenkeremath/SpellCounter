package com.kenkeremath.mtgcounter.util

import android.util.Log
import com.kenkeremath.mtgcounter.BuildConfig

object LogUtils {

    const val TAG_DEFAULT = "SC_Debug"
    const val TAG_INCREMENTER = "SC_Incrementer_Debug"
    const val TAG_TABLETOP_TOUCH_EVENTS = "SC_Tabletop_TE_Debug"
    const val TAG_PULL_TO_REVEAL = "SC_PTR_Debug"
    const val TAG_IMAGES = "SC_IMG_Debug"

    private val whiteList = setOf(
        TAG_DEFAULT,
//        TAG_INCREMENTER,
        TAG_TABLETOP_TOUCH_EVENTS,
        TAG_PULL_TO_REVEAL,
        TAG_IMAGES,
    )

    fun d(message: String, tag: String = TAG_DEFAULT) {
        if (BuildConfig.DEBUG && whiteList.contains(tag)) {
            Log.d(tag, message)
        }
    }
}