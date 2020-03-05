package com.kenkeremath.mtgcounter.ui.setup

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kenkeremath.mtgcounter.R

class SetupFragment : Fragment() {

    companion object {
        fun newInstance() = SetupFragment()
    }

    private lateinit var viewModel: SetupViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.setup_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SetupViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
