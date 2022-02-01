package com.kenkeremath.mtgcounter.ui.setup.tabletop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.player.PlayerSetupModel
import com.kenkeremath.mtgcounter.ui.game.GameActivity
import com.kenkeremath.mtgcounter.ui.setup.SetupViewModel
import com.kenkeremath.mtgcounter.view.TabletopLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupTabletopFragment : Fragment(), OnPlayerSelectedListener {

    private val viewModel: SetupViewModel by activityViewModels()

    private lateinit var tabletopLayout: TabletopLayout
    private lateinit var tabletopAdapter: SetupTabletopLayoutAdapter

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
        toolbar.setTitle(R.string.customize_table_title)
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        tabletopLayout = view.findViewById(R.id.tabletop_layout)
        tabletopAdapter = SetupTabletopLayoutAdapter(tabletopLayout, this)

        startButton = view.findViewById(R.id.start_button)
        startButton.setOnClickListener {
            viewModel.setupPlayers.value?.let {
                startActivity(GameActivity.startIntentFromSetup(requireContext(), it))
            }
        }

        viewModel.setupPlayers.observe(viewLifecycleOwner, {
            tabletopAdapter.setPositions(viewModel.selectedTabletopType)
            tabletopAdapter.updateAll(viewModel.selectedTabletopType, it)
        })
    }

    override fun onPlayerSelected(playerId: Int) {
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