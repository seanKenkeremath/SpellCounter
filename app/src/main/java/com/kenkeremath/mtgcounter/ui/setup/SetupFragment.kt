package com.kenkeremath.mtgcounter.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kenkeremath.mtgcounter.model.PlayerSetupModel
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.TabletopType

class SetupFragment : Fragment(), CompoundButton.OnCheckedChangeListener {

    lateinit var playerNumberButtons: List<Button>
    lateinit var fivePlusPlayersButton: Button

    lateinit var lifeButtons: List<Button>
    lateinit var customLifeButton: Button

    lateinit var keepScreenAwakeCheckbox: CheckBox

    lateinit var tabletopModeButtons: List<Button>

    companion object {
        fun newInstance() = SetupFragment()
    }

    private lateinit var viewModel: SetupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.setup_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerNumberButtons = listOf(
            view.findViewById(R.id.one_player_button),
            view.findViewById(R.id.two_player_button),
            view.findViewById(R.id.three_player_button),
            view.findViewById(R.id.four_player_button)
        )
        fivePlusPlayersButton = view.findViewById(R.id.five_plus_player_button)
        //Save us some boilerplate by assuming buttons will be in order and start at 1 player
        playerNumberButtons.forEachIndexed { index, button ->
            button.tag = index + 1
            button.setOnClickListener { viewModel.setNumberOfPlayers(button.tag as Int) }
        }
        fivePlusPlayersButton.setOnClickListener { v ->
            viewModel.setNumberOfPlayers(5)
            //TODO: open dialog or input box
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

        keepScreenAwakeCheckbox = view.findViewById(R.id.keep_screen_awake_checkbox)
        keepScreenAwakeCheckbox.setOnCheckedChangeListener(this)

        //TODO: tag with correct enums
        //TODO: all layouts
        tabletopModeButtons = listOf(
            view.findViewById(R.id.tabletop_list),
            view.findViewById(R.id.tabletop_wide)
        )
        tabletopModeButtons[0].tag = TabletopType.LIST
        tabletopModeButtons[1].tag = TabletopType.ONE_HORIZONTAL

        for (tabletopModeButton in tabletopModeButtons) {
            tabletopModeButton.setOnClickListener { viewModel.setTabletopType(tabletopModeButton.tag as TabletopType) }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SetupViewModel::class.java)

        viewModel.players.observe(viewLifecycleOwner, Observer<List<PlayerSetupModel>> { players ->
            players?.size?.let {
                for (playerNumberButton in playerNumberButtons) {
                    playerNumberButton.isSelected = playerNumberButton.tag == it
                }
                fivePlusPlayersButton.isSelected = it >= 5
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
        }
    }
}
