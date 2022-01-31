package com.kenkeremath.mtgcounter.persistence.images

import kotlinx.coroutines.flow.Flow
import java.io.File

interface ImageRepository {
    fun saveUrlImageToDisk(counterId: Int, url: String): Flow<File?>
    fun saveLocalImageToDisk(counterId: Int, uri: String): Flow<File?>
    fun deleteImagesForCounter(counterId: Int): Boolean
}