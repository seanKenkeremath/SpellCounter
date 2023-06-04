package com.kenkeremath.mtgcounter.ui.setup.tabletop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.model.player.PlayerSetupModel
import com.kenkeremath.mtgcounter.ui.game.GameActivity
import com.kenkeremath.mtgcounter.ui.setup.SetupViewModel
import com.kenkeremath.mtgcounter.view.TabletopLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupTabletopFragment : Fragment(), OnSetupPlayerSelectedListener {

    private val viewModel: SetupViewModel by activityViewModels()

    private lateinit var tabletopLayout: TabletopLayout
    private lateinit var tabletopAdapter: SetupTabletopLayoutAdapter
    private lateinit var tabletopRecyclerView: RecyclerView
    private lateinit var tabletopRecyclerAdapter: SetupTabletopRecyclerViewAdapter

    private lateinit var startButton: View
    private lateinit var toolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_tabletop_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        tabletopLayout = view.findViewById(R.id.tabletop_layout)
        tabletopAdapter = SetupTabletopLayoutAdapter(tabletopLayout, this)

        tabletopRecyclerView = view.findViewById(R.id.tabletop_layout_recycler)
        tabletopRecyclerAdapter = SetupTabletopRecyclerViewAdapter(this)
        tabletopRecyclerView.adapter = tabletopRecyclerAdapter
        tabletopRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        if (viewModel.selectedTabletopType == TabletopType.LIST) {
            tabletopRecyclerView.visibility = View.VISIBLE
            tabletopLayout.visibility = View.GONE
        } else {
            tabletopRecyclerView.visibility = View.GONE
            tabletopLayout.visibility = View.VISIBLE
        }

        startButton = view.findViewById(R.id.start_button)
        startButton.setOnClickListener {
            startActivity(
                GameActivity.startIntentFromSetup(
                    requireContext(),
                    viewModel.getSetupPlayersWithColorCounters()
                )
            )
        }

        viewModel.setupPlayers.observe(viewLifecycleOwner) {
            tabletopAdapter.setPositions(viewModel.selectedTabletopType)
            tabletopAdapter.updateAll(viewModel.selectedTabletopType, it)

            tabletopRecyclerAdapter.setPlayers(it)
        }
    }

    override fun onSetupPlayerSelected(playerId: Int) {
        viewModel.findSetupPlayerById(playerId)?.let { existingPlayer ->
            val f = SelectPlayerOptionsDialogFragment.newInstance(existingPlayer)
            f.show(childFragmentManager, SelectPlayerOptionsDialogFragment.TAG)
            f.setFragmentResultListener(
                SelectPlayerOptionsDialogFragment.REQUEST_CUSTOMIZE
            ) { _, bundle ->
                val updatedPlayer =
                    bundle.getParcelable<PlayerSetupModel>(SelectPlayerOptionsDialogFragment.RESULT_MODEL)
                updatedPlayer?.let {
                    viewModel.updatePlayer(updatedPlayer)
                }
            }
        }
    }

}