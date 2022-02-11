package com.kenkeremath.mtgcounter.ui.settings.counters.manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.model.player.PlayerProfileModel
import com.kenkeremath.mtgcounter.ui.settings.counters.edit.EditCounterDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageCountersFragment : Fragment(), OnManageCounterClickedListener {

    companion object {
        fun newInstance(): ManageCountersFragment {
            val args = Bundle()
            val fragment = ManageCountersFragment()
            fragment.arguments = args
            return fragment
        }

        const val TAG = "fragment_manage_counters"
    }

    private val viewModel: ManageCountersViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private val recyclerAdapter = ManageCountersRecyclerAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_manage_counters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        recyclerView = view.findViewById(R.id.manage_counters_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = recyclerAdapter
        val dividers = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.nav_menu_divider)?.let {
            dividers.setDrawable(it)
        }
        recyclerView.addItemDecoration(dividers)

        viewModel.counters.observe(viewLifecycleOwner, {
            recyclerAdapter.setCounters(it)
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshCounters()
    }

    override fun onCounterRemoveClicked(id: Int) {
        viewModel.removeCounter(id)
    }

    override fun onCounterCreateClicked() {
        val f = EditCounterDialogFragment.newInstance(PlayerProfileModel.NAME_DEFAULT)
        f.show(childFragmentManager, EditCounterDialogFragment.TAG)
        f.setFragmentResultListener(
            EditCounterDialogFragment.REQUEST_KEY_COUNTER
        ) { _, bundle ->
            val newCounter =
                bundle.getParcelable<CounterTemplateModel>(EditCounterDialogFragment.RESULT_COUNTER)
            newCounter?.let {
                viewModel.addNewCounter(it)
            }
        }
    }
}