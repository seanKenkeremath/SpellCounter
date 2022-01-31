package com.kenkeremath.mtgcounter.persistence.images

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

interface ImageApi {
    @GET
    suspend fun downloadFileFromUrl(
        @Url url: String
    ): ResponseBody
}