package com.kenkeremath.mtgcounter.persistence.images

import kotlinx.coroutines.flow.Flow
import java.io.File

interface ImageRepository {
    fun saveUrlImageToDisk(url: String): Flow<File>
    fun saveLocalImageToDisk(uri: String): Flow<File>
    fun deleteFile(path: String): Boolean
}