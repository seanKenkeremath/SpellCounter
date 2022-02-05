package com.kenkeremath.mtgcounter.ui.settings.profiles.manage

interface OnProfileClickedListener {
    fun onProfileClicked(name: String)
    fun onProfileDeleteClicked(name: String)
    fun onProfileCreateClicked()
}