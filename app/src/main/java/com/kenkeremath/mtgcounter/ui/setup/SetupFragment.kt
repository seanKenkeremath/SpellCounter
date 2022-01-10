package com.kenkeremath.mtgcounter.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.ui.game.GameActivity
import com.kenkeremath.mtgcounter.ui.setup.tabletop.SetupTabletopFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupFragment : Fragment(), CompoundButton.OnCheckedChangeListener {

    private lateinit var playerNumberButtons: List<Button>
    private lateinit var lifeButtons: List<Button>
    private lateinit var customLifeButton: Button
    private lateinit var keepScreenAwakeCheckbox: CheckBox
    private lateinit var hideNavigationCheckbox: CheckBox
    private lateinit var customizeLayoutButton: View
    private lateinit var tabletopContainer: ViewGroup
    private lateinit var tabletopModeButtons: List<Button>
    private lateinit var startButton: Button

    private val viewModel: SetupViewModel by activityViewModels()


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

        customizeLayoutButton = view.findViewById(R.id.customize_tabletop_button)
        customizeLayoutButton.setOnClickListener {
            val f = SetupTabletopFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.container, f)
                .addToBackStack("Setup_Tabletop")
                .commit()
        }
        viewModel.showCustomizeLayoutButton.observe(viewLifecycleOwner, {
            customizeLayoutButton.visibility = if (it) View.VISIBLE else View.GONE
        })

        startButton = view.findViewById(R.id.start_button)
        startButton.setOnClickListener {
            if (viewModel.selectedTabletopType != TabletopType.NONE) {
                viewModel.setupPlayers.value?.let { players ->
                    startActivity(GameActivity.startIntentFromSetup(requireContext(), players))
                }
            }
        }

        viewModel.numberOfPlayers.observe(viewLifecycleOwner, { numberOfPlayers ->
            numberOfPlayers?.let {
                for (playerNumberButton in playerNumberButtons) {
                    playerNumberButton.isSelected = playerNumberButton.tag == it
                }
            }
        })

        viewModel.startingLife.observe(viewLifecycleOwner, {
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

        viewModel.keepScreenOn.observe(viewLifecycleOwner, {
            it?.let {

                //Remove listener to avoid infinite loop
                keepScreenAwakeCheckbox.setOnCheckedChangeListener(null)
                if (keepScreenAwakeCheckbox.isChecked != it) {
                    keepScreenAwakeCheckbox.isChecked = it
                }
                keepScreenAwakeCheckbox.setOnCheckedChangeListener(this)
            }
        })

        viewModel.hideNavigation.observe(viewLifecycleOwner, {
            it?.let {

                //Remove listener to avoid infinite loop
                hideNavigationCheckbox.setOnCheckedChangeListener(null)
                if (hideNavigationCheckbox.isChecked != it) {
                    hideNavigationCheckbox.isChecked = it
                }
                hideNavigationCheckbox.setOnCheckedChangeListener(this)
            }
        })

        viewModel.tabletopTypes.observe(viewLifecycleOwner, {
            it?.let {
                for (tabletopModeButton in tabletopModeButtons) {
                    tabletopModeButton.visibility = View.GONE
                }
                for (i in 0 until kotlin.math.min(3, it.size)) {
                    tabletopModeButtons[i].tag = it[i].tabletopType
                    tabletopModeButtons[i].visibility = View.VISIBLE
                    tabletopModeButtons[i].isSelected = it[i].selected
                    tabletopModeButtons[i].setOnClickListener {
                        viewModel.setTabletopType(
                            tabletopModeButtons[i].tag as TabletopType
                        )
                    }
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
