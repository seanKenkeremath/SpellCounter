package com.kenkeremath.mtgcounter.persistence.images

import kotlinx.coroutines.flow.Flow
import java.io.File

interface ImageRepository {
    fun saveUrlImageToDisk(url: String): Flow<ImageSaveResult>
    fun saveLocalImageToDisk(uri: String): Flow<ImageSaveResult>
    fun deleteFile(path: String): Boolean
}