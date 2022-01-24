package com.kenkeremath.mtgcounter.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.ui.game.GameActivity
import com.kenkeremath.mtgcounter.ui.setup.tabletop.SetupTabletopFragment
import com.kenkeremath.mtgcounter.view.TabletopLayout
import com.kenkeremath.mtgcounter.view.layoutbutton.TabletopLayoutButtonAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.min

@AndroidEntryPoint
class SetupFragment : Fragment(), CompoundButton.OnCheckedChangeListener {

    companion object {
        fun newInstance() = SetupFragment()
    }

    private lateinit var playerNumberButtons: List<Button>
    private lateinit var lifeButtons: List<Button>
    private lateinit var customLifeButton: Button
    private lateinit var keepScreenAwakeCheckbox: CheckBox
    private lateinit var hideNavigationCheckbox: CheckBox
    private lateinit var customizeLayoutButton: View
    private lateinit var tabletopContainer: ViewGroup
    private lateinit var tabletopLayoutButtonA: View
    private lateinit var tabletopLayoutAdapterA: TabletopLayoutButtonAdapter
    private lateinit var tabletopLayoutButtonB: View
    private lateinit var tabletopLayoutAdapterB: TabletopLayoutButtonAdapter
    private lateinit var tabletopListLayoutButton: LinearLayout
    private lateinit var startButton: Button
    private lateinit var toolbar: Toolbar

    private val viewModel: SetupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolbar)
        toolbar.setTitle(R.string.setup_game_title)
        toolbar.setNavigationIcon(R.mipmap.ic_launcher)

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

        val tabletopLayoutA = view.findViewById<TabletopLayout>(R.id.tabletop_a)
        tabletopLayoutButtonA = view.findViewById(R.id.tabletop_a_button)
        tabletopLayoutAdapterA = TabletopLayoutButtonAdapter(tabletopLayoutA)
        val tabletopLayoutB = view.findViewById<TabletopLayout>(R.id.tabletop_b)
        tabletopLayoutButtonB = view.findViewById(R.id.tabletop_b_button)
        tabletopLayoutAdapterB = TabletopLayoutButtonAdapter(tabletopLayoutB)
        tabletopListLayoutButton = view.findViewById(R.id.tabletop_list)

        /**
         * Prevents the tabletop layout from intercepting touch events (so we can put it in a
         * clickable container)
         */
        tabletopLayoutA.isEnabled = false
        tabletopLayoutB.isEnabled = false

        customizeLayoutButton = view.findViewById(R.id.customize_tabletop_button)
        customizeLayoutButton.setOnClickListener {
            val f = SetupTabletopFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, f)
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
                tabletopLayoutButtonA.visibility = View.GONE
                tabletopLayoutButtonB.visibility = View.GONE
                tabletopListLayoutButton.visibility = View.GONE
                var aIsSet = false
                for (i in it.indices) {
                    val type = it[i].tabletopType
                    if (type == TabletopType.LIST) {
                        setTabletopLayoutButtonContent(it[i], tabletopListLayoutButton)
                        for (child in tabletopListLayoutButton.children) {
                            child.visibility = View.GONE
                        }
                        /**
                         * List button has 4 player images + 1 ellipsis as part of vertical linear layout.
                         * Set as many child views to visible as there are players. if greater than 4,
                         * the ellipsis will show
                         */
                        for (buttonPlayerIndex in 0 until min(tabletopListLayoutButton.childCount, viewModel.numberOfPlayers.value!!)) {
                            tabletopListLayoutButton.getChildAt(buttonPlayerIndex).visibility = View.VISIBLE
                        }
                    } else if (!aIsSet) {
                        setTabletopLayoutButtonContent(it[i], tabletopLayoutButtonA)
                        tabletopLayoutAdapterA.setPositions(it[i].tabletopType)
                        tabletopLayoutAdapterA.updateAll(
                            type,
                            List(viewModel.numberOfPlayers.value!!) {})
                        aIsSet = true
                    } else {
                        setTabletopLayoutButtonContent(it[i], tabletopLayoutButtonB)
                        tabletopLayoutAdapterB.setPositions(it[i].tabletopType)
                        tabletopLayoutAdapterB.updateAll(
                            type,
                            List(viewModel.numberOfPlayers.value!!) {})
                    }
                }
            }
        })
    }

    private fun setTabletopLayoutButtonContent(
        uiModel: TabletopLayoutSelectionUiModel,
        button: View
    ) {
        button.tag = uiModel.tabletopType
        button.visibility = View.VISIBLE
        button.isSelected = uiModel.selected
        button.setOnClickListener {
            viewModel.setTabletopType(
                button.tag as TabletopType
            )
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (buttonView == keepScreenAwakeCheckbox) {
            viewModel.setKeepScreenOn(isChecked)
        } else if (buttonView == hideNavigationCheckbox) {
            viewModel.setHideNavigation(isChecked)
        }
    }
}
