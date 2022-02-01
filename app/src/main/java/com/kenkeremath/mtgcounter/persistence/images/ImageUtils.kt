package com.kenkeremath.mtgcounter.persistence.images

import android.content.Context
import java.io.File

object ImageUtils {
    val Context.imagesDir: File
        get() {
            val dir = File(
                applicationContext.filesDir,
                "counter_images/"
            )
            if (!dir.exists()) {
                dir.mkdir()
            }
            return dir
        }

    //Above this the image will not be copied locally
    const val IMAGE_RAW_LIMIT = 8_000_000L
    const val IMAGE_COMPRESS_THRESHOLD = 1_000_000L
}