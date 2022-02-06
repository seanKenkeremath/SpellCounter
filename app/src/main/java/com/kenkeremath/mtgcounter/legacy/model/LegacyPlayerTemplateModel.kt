package com.kenkeremath.mtgcounter.legacy.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LegacyPlayerTemplateModel(
    @Json(name = "templateName") val templateName: String? = null,
    @Json(name = "startingLife") val startingLife: Int = 0, //Ignored
    @Json(name = "counters") val counters: List<LegacyCounterTemplateModel>? = null,
)