package com.kenkeremath.mtgcounter.persistence.images

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.math.MathUtils.clamp
import com.kenkeremath.mtgcounter.coroutines.DefaultDispatcherProvider
import com.kenkeremath.mtgcounter.coroutines.DispatcherProvider
import com.kenkeremath.mtgcounter.persistence.images.ImageUtils.IMAGE_COMPRESS_THRESHOLD
import com.kenkeremath.mtgcounter.persistence.images.ImageUtils.IMAGE_RAW_LIMIT
import com.kenkeremath.mtgcounter.persistence.images.ImageUtils.imagesDir
import com.kenkeremath.mtgcounter.util.LogUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okio.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

class ImageRepositoryImpl(
    private val appContext: Context,
    private val imageApi: ImageApi,
    private val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider()

) :
    ImageRepository {
    private fun generateImageDir(): File {
        val f = File(appContext.imagesDir, "${UUID.randomUUID()}")
        if (!f.exists()) {
            f.mkdirs()
        }
        return f
    }

    override fun saveUrlImageToDisk(url: String): Flow<File> {
        LogUtils.d("saving image url $url", LogUtils.TAG_IMAGES)
        val imageDir = generateImageDir()
        val targetFile = File(imageDir, "image")
        return flow {
            downloadImageToFile(targetFile, url)
            emit(targetFile)
        }
            .catch { e ->
                LogUtils.d("Save Failed: $e", LogUtils.TAG_IMAGES)
                deleteFile(imageDir.absolutePath)
                throw ImageSaveFailedException()
            }
            .flowOn(dispatcherProvider.io())
    }

    private suspend fun downloadImageToFile(outputFile: File, url: String) =
        withContext(dispatcherProvider.io()) {
            val inputStream = imageApi.downloadFileFromUrl(url).byteStream()
            downloadBitmapToFile(outputFile, inputStream)
        }

    private suspend fun downloadBitmapToFile(outputFile: File, inputStream: InputStream) =
        withContext(dispatcherProvider.io()) {
            val bitmapFactoryOptions = BitmapFactory.Options()
            val bitmap =
                BitmapFactory.decodeStream(
                    inputStream,
                    null,
                    bitmapFactoryOptions
                )
            bitmap?.let {
                LogUtils.d("Bitmap Downloaded", LogUtils.TAG_IMAGES)
                val transparency = bitmapFactoryOptions.outMimeType.lowercase().contains("png")
                LogUtils.d(
                    "Bitmap Data: transparency: $transparency, mime: $${bitmapFactoryOptions.outMimeType}, size: ${bitmap.byteCount}",
                    LogUtils.TAG_IMAGES
                )
                val outStream = FileOutputStream(outputFile)
                outStream.use {
                    val size = bitmap.byteCount
                    if (size > IMAGE_RAW_LIMIT) {
                        LogUtils.d("Bitmap too large to compress", LogUtils.TAG_IMAGES)
                        throw ImageTooLargeException()
                    } else {
                        val quality = if (size < IMAGE_COMPRESS_THRESHOLD)
                            90
                        else {
                            (90 - 70 * clamp(
                                (size - IMAGE_COMPRESS_THRESHOLD) / IMAGE_RAW_LIMIT.toDouble(),
                                0.0,
                                1.0
                            )).toInt()
                        }
                        LogUtils.d("Compressing with quality: $quality", LogUtils.TAG_IMAGES)
                        bitmap.compress(
                            if (transparency) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG,
                            quality,
                            outStream
                        )
                        LogUtils.d(
                            "Compress complete. Output file size: ${outputFile.length()}",
                            LogUtils.TAG_IMAGES
                        )
                    }
                }
            } ?: throw ImageSaveFailedException()
        }

    override fun saveLocalImageToDisk(uri: String): Flow<File> {
        LogUtils.d("saving local image $uri", LogUtils.TAG_IMAGES)
        val imageDir = generateImageDir()
        val targetFile = File(imageDir, "image")
        return flow {
            val stream: InputStream? = appContext.contentResolver.openInputStream(Uri.parse(uri))
            stream?.let {
                downloadBitmapToFile(targetFile, stream)
                emit(targetFile)
            } ?: throw ImageSaveFailedException()
        }
            .catch { e ->
                LogUtils.d("Save Failed: $e", LogUtils.TAG_IMAGES)
                deleteFile(imageDir.absolutePath)
                throw ImageSaveFailedException()
            }
            .flowOn(dispatcherProvider.io())
    }

    override fun deleteFile(path: String): Boolean {
        val dir = File(path)
        if (dir.exists()) {
            return dir.deleteRecursively()
        }
        return false
    }
}