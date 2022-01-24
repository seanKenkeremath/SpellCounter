package com.kenkeremath.mtgcounter.ui.game.options

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.ui.game.GameViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameOptionsDialogFragment : DialogFragment(), CompoundButton.OnCheckedChangeListener {
    companion object {
        fun newInstance(): GameOptionsDialogFragment {
            val f = GameOptionsDialogFragment()
            f.arguments = Bundle()
            return f
        }
    }

    private var listener: Listener? = null
    private val viewModel: GameViewModel by activityViewModels()

    private lateinit var closeButton: View
    private lateinit var resetButton: View
    private lateinit var exitButton: View
    private lateinit var keepScreenAwakeCheckbox: CheckBox
    private lateinit var hideNavigationCheckbox: CheckBox

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.dialog_game_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        closeButton = view.findViewById(R.id.close_button)
        closeButton.setOnClickListener {
            dismiss()
        }
        resetButton = view.findViewById(R.id.reset_game_button)
        resetButton.setOnClickListener {
            listener?.onOpenResetPrompt()
        }
        exitButton = view.findViewById(R.id.exit_game_button)
        exitButton.setOnClickListener {
            listener?.onOpenExitPrompt()
        }

        keepScreenAwakeCheckbox = view.findViewById(R.id.keep_screen_awake_checkbox)
        keepScreenAwakeCheckbox.setOnCheckedChangeListener(this)

        hideNavigationCheckbox = view.findViewById(R.id.hide_navigation_checkbox)
        hideNavigationCheckbox.setOnCheckedChangeListener(this)

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
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (buttonView == keepScreenAwakeCheckbox) {
            viewModel.setKeepScreenOn(isChecked)
        } else if (buttonView == hideNavigationCheckbox) {
            viewModel.setHideNavigation(isChecked)
        }
    }

    interface Listener {
        fun onOpenExitPrompt()
        fun onOpenResetPrompt()
    }
}