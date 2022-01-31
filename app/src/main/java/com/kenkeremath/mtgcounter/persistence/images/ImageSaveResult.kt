package com.kenkeremath.mtgcounter.persistence.images

import java.io.File

data class ImageSaveResult(
    val file: File? = null,
    val source: ImageSource,
)

enum class ImageSource {
    LOCAL_FILE,
    RAW_URI
}