package com.kenkeremath.mtgcounter.ui.settings.profiles.manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.ui.settings.profiles.edit.EditProfileActivity
import com.kenkeremath.mtgcounter.ui.settings.profiles.edit.EditProfileDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageProfilesFragment : Fragment(), OnProfileClickedListener {

    companion object {
        fun newInstance(): ManageProfilesFragment {
            val args = Bundle()
            val fragment = ManageProfilesFragment()
            fragment.arguments = args
            return fragment
        }

        const val TAG = "fragment_manage_profiles"
    }

    private val viewModel: ManageProfilesViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private val recyclerAdapter = ManageProfilesRecyclerAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_manage_profiles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        recyclerView = view.findViewById(R.id.profiles_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = recyclerAdapter
        val dividers = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.nav_menu_divider)?.let {
            dividers.setDrawable(it)
        }
        recyclerView.addItemDecoration(dividers)

        viewModel.profiles.observe(viewLifecycleOwner) {
            recyclerAdapter.setProfiles(it)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshProfiles()
    }

    override fun onProfileClicked(name: String) {
        viewModel.getProfileByName(name)?.let {
            startActivity(EditProfileActivity.getStartIntent(requireContext(), it))
        }
    }

    override fun onProfileDeleteClicked(name: String) {
        viewModel.deleteProfile(name)
    }

    override fun onProfileCreateClicked() {
        startActivity(EditProfileActivity.getStartIntent(requireContext()))
    }
}