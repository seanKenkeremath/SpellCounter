package com.kenkeremath.mtgcounter.view

import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.widget.EditText

fun EditText.setInputLimit(limit: Int) {
    val svFilters = arrayOfNulls<InputFilter>(1)
    svFilters[0] = LengthFilter(limit)
    this.filters = svFilters
}