package com.kenkeremath.mtgcounter.ui.settings.counters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.kenkeremath.mtgcounter.R

class CreateCounterDialogFragment: DialogFragment() {

    companion object {
        fun newInstance(): CreateCounterDialogFragment {
            val args = Bundle()
            val fragment = CreateCounterDialogFragment()
            fragment.arguments = args
            return fragment
        }
        const val TAG = "fragment_create_counter"
        const val REQUEST_KEY_COUNTER = "request_key_counter"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_create_counter, container, false)
        return view
    }
}