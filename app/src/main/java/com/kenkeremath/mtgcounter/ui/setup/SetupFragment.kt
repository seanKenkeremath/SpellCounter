package com.kenkeremath.mtgcounter.ui.setup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.ui.game.GameActivity
import com.kenkeremath.mtgcounter.ui.setup.tabletop.SetupTabletopFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupFragment : Fragment(), CompoundButton.OnCheckedChangeListener {

    lateinit var playerNumberButtons: List<Button>

    lateinit var lifeButtons: List<Button>
    lateinit var customLifeButton: Button

    lateinit var keepScreenAwakeCheckbox: CheckBox
    lateinit var hideNavigationCheckbox: CheckBox

    lateinit var tabletopContainer: ViewGroup
    lateinit var tabletopModeButtons: List<Button>

    lateinit var startButton: Button

    private val viewModel: SetupViewModel by viewModels()


    companion object {
        fun newInstance() = SetupFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerNumberButtons = listOf(
            view.findViewById(R.id.one_player_button),
            view.findViewById(R.id.two_player_button),
            view.findViewById(R.id.three_player_button),
            view.findViewById(R.id.four_player_button),
            view.findViewById(R.id.five_player_button),
            view.findViewById(R.id.six_player_button),
            view.findViewById(R.id.seven_player_button),
            view.findViewById(R.id.eight_player_button)
        )

        //Save us some boilerplate by assuming buttons will be in order and start at 1 player
        playerNumberButtons.forEachIndexed { index, button ->
            button.tag = index + 1
            button.setOnClickListener { viewModel.setNumberOfPlayers(button.tag as Int) }
        }

        lifeButtons = listOf(
            view.findViewById(R.id.twenty_life_button),
            view.findViewById(R.id.forty_life_button)
        )
        lifeButtons[0].tag = 20
        lifeButtons[1].tag = 40
        for (lifeButton in lifeButtons) {
            lifeButton.setOnClickListener { viewModel.setStartingLife(lifeButton.tag as Int) }
        }
        customLifeButton = view.findViewById(R.id.custom_life_button)
        customLifeButton.setOnClickListener {
//            viewModel.insert()
        }

        keepScreenAwakeCheckbox = view.findViewById(R.id.keep_screen_awake_checkbox)
        keepScreenAwakeCheckbox.setOnCheckedChangeListener(this)

        hideNavigationCheckbox = view.findViewById(R.id.hide_navigation_checkbox)
        hideNavigationCheckbox.setOnCheckedChangeListener(this)

        tabletopContainer = view.findViewById(R.id.tabletop_container)

        tabletopModeButtons = listOf(
            view.findViewById(R.id.tabletop_list),
            view.findViewById(R.id.tabletop_a),
            view.findViewById(R.id.tabletop_b)
        )

        startButton = view.findViewById(R.id.start_button)
        startButton.setOnClickListener {
            viewModel.tabletopType.value?.let {
                if (it == TabletopType.LIST) {
                    viewModel.setupPlayers.value?.let { players ->
                        startActivity(GameActivity.startIntentFromSetup(requireContext(), players))
                    }
                } else {
                    if (viewModel.tabletopType.value != TabletopType.NONE && viewModel.tabletopType.value != TabletopType.LIST) {
                        val f = SetupTabletopFragment()
                        requireActivity().supportFragmentManager.beginTransaction()
                            .add(R.id.container, f)
                            .addToBackStack("Setup_Tabletop")
                            .commit()
                    }
                }
            }
        }

        viewModel.numberOfPlayers.observe(viewLifecycleOwner, Observer<Int> { numberOfPlayers ->
            numberOfPlayers?.let {
                for (playerNumberButton in playerNumberButtons) {
                    playerNumberButton.isSelected = playerNumberButton.tag == it
                }
            }
        })

        viewModel.startingLife.observe(viewLifecycleOwner, Observer<Int> {
            it?.let {
                var match = false
                for (lifeButton in lifeButtons) {
                    lifeButton.isSelected = lifeButton.tag == it
                    match = true
                }
                if (!match) {
                    customLifeButton.isSelected = true
                }
            }
        })

        viewModel.keepScreenOn.observe(viewLifecycleOwner, Observer<Boolean> {
            it?.let {

                //Remove listener to avoid infinite loop
                keepScreenAwakeCheckbox.setOnCheckedChangeListener(null)
                if (keepScreenAwakeCheckbox.isChecked != it) {
                    keepScreenAwakeCheckbox.isChecked = it
                }
                keepScreenAwakeCheckbox.setOnCheckedChangeListener(this)
            }
        })

        viewModel.hideNavigation.observe(viewLifecycleOwner, Observer<Boolean> {
            it?.let {

                //Remove listener to avoid infinite loop
                hideNavigationCheckbox.setOnCheckedChangeListener(null)
                if (hideNavigationCheckbox.isChecked != it) {
                    hideNavigationCheckbox.isChecked = it
                }
                hideNavigationCheckbox.setOnCheckedChangeListener(this)
            }
        })

        viewModel.availableTabletopTypes.observe(viewLifecycleOwner, Observer<List<TabletopType>> {
            it?.let {
                for (tabletopModeButton in tabletopModeButtons) {
                    tabletopModeButton.visibility = View.GONE
                }
                for (i in 0 until kotlin.math.min(3, it.size)) {
                    tabletopModeButtons[i].tag = it[i]
                    tabletopModeButtons[i].visibility = View.VISIBLE
                    tabletopModeButtons[i].setOnClickListener {
                        viewModel.setTabletopType(
                            tabletopModeButtons[i].tag as TabletopType
                        )
                    }
                }
            }
        })

        viewModel.tabletopType.observe(viewLifecycleOwner, Observer<TabletopType> {
            it?.let {
                for (tableTopModeButton in tabletopModeButtons) {
                    tableTopModeButton.isSelected = tableTopModeButton.tag == it
                }
            }
        })
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (buttonView == keepScreenAwakeCheckbox) {
            viewModel.setKeepScreenOn(isChecked)
        } else if (buttonView == hideNavigationCheckbox) {
            viewModel.setHideNavigation(isChecked)
        }
    }
}
