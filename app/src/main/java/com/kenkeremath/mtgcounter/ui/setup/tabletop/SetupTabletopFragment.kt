package com.kenkeremath.mtgcounter.ui.setup.tabletop

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.ui.game.GameActivity
import com.kenkeremath.mtgcounter.ui.setup.SetupViewModel
import com.kenkeremath.mtgcounter.view.TabletopLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupTabletopFragment : Fragment() {

    private val viewModel: SetupViewModel by viewModels()

    private lateinit var tabletopLayout: TabletopLayout
    private lateinit var tabletopAdapter: SetupTabletopLayoutAdapter

    private lateinit var startButton: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_tabletop_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabletopLayout = view.findViewById(R.id.tabletop_layout)
        tabletopAdapter = SetupTabletopLayoutAdapter(tabletopLayout)
        viewModel.tabletopType.value?.let {
            tabletopAdapter.setPositions(it)
            tabletopAdapter.updateAll(it, viewModel.setupPlayers.value!!)
        }
        startButton = view.findViewById(R.id.start_button)
        startButton.setOnClickListener {
            //TODO: pass player setup model
            startActivity(Intent(context, GameActivity::class.java))
        }
    }

}