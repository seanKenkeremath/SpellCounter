package com.kenkeremath.mtgcounter.ui.settings.counters.manage

interface OnManageCounterClickedListener {
    fun onCounterClicked(id: Int)
    fun onCounterRemoveClicked(id: Int)
    fun onCounterCreateClicked()
}