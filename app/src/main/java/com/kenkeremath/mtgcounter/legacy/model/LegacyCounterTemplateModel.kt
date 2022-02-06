package com.kenkeremath.mtgcounter.legacy.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LegacyCounterTemplateModel(
    @Json(name = "name") val name: String? = null, //Used as text label
    @Json(name = "startingValue") val startingValue: Int = 0,
    @Json(name = "color") val color: Int = 0, //Ignored
)