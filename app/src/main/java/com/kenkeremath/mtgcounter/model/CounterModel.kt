package com.kenkeremath.mtgcounter.model

import android.os.Parcelable
import com.kenkeremath.mtgcounter.util.CounterUtils
import kotlinx.parcelize.Parcelize

@Parcelize
data class CounterModel(
    val id: Int,
    val startingAmount: Int = 0,
    var amount: Int = 0,
    val name: String? = null,
    val color: Int = 0,
    val templateId : Int? = null
): Parcelable {

    companion object  {
        fun createNew(): CounterModel {
            return CounterModel(CounterUtils.getUniqueId())
        }
    }
}