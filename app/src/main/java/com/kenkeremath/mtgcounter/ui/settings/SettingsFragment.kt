package com.kenkeremath.mtgcounter.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.ui.settings.counters.manage.ManageCountersFragment
import com.kenkeremath.mtgcounter.ui.settings.profiles.manage.ManageProfilesFragment

class SettingsFragment: Fragment() {

    companion object {
        fun newInstance(): SettingsFragment {
            val args = Bundle()
            val fragment = SettingsFragment()
            fragment.arguments = args
            return fragment
        }

        const val TAG = "fragment_settings"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val manageProfiles: View = view.findViewById(R.id.manage_profiles)
        val manageCounters: View = view.findViewById(R.id.manage_counters)
        manageProfiles.setOnClickListener {
            val f = ManageProfilesFragment.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, f)
                .addToBackStack(ManageProfilesFragment.TAG)
                .commit()
        }
        manageCounters.setOnClickListener {
            val f = ManageCountersFragment.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, f)
                .addToBackStack(ManageCountersFragment.TAG)
                .commit()
        }
        return view
    }


}