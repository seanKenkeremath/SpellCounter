package com.kenkeremath.mtgcounter.ui.settings.counters.edit

import com.kenkeremath.mtgcounter.R

enum class CreateCounterType(val labelResId: Int) {
    TEXT(R.string.create_counter_type_text),
    IMAGE(R.string.create_counter_type_image),
    URL(R.string.create_counter_type_url);
}