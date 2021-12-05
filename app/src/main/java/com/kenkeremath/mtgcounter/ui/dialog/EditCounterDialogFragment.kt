package com.kenkeremath.mtgcounter.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AlertDialog
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.CounterModel
import com.kenkeremath.mtgcounter.view.LineColorPickerView
import com.kenkeremath.mtgcounter.view.setInputLimit
import java.util.*

class EditCounterDialogFragment : BaseCounterDialogFragment() {

    private lateinit var colorPickerView: LineColorPickerView
    private lateinit var counterNameInput: EditText
    private lateinit var counterStartingValueInput: EditText

    //These values will not change (so we do not need to save them on orientation change)
    private var isCreateNew: Boolean = false
    private var playerId: Int = -1
    private lateinit var counter: CounterModel
    private var counterPosition = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_edit_counter, null)

        colorPickerView = view.findViewById(R.id.color_picker_view)
        counterNameInput = view.findViewById(R.id.input_counter_name)
        counterStartingValueInput = view.findViewById(R.id.input_counter_starting_value)

        /**
         * These will either be the values of the initial counter or values that have been edited if this is after orientation change
         */
        val initialColor: Int
        var initialName: String? = null
        val initialStartingValue: Int
        if (arguments != null) {

            //These values will not change (so we do not need to save them on orientation change)
            playerId = requireArguments().getInt(ARGS_PLAYER_ID, -1)
            //Counter will only exist when editing
            counter = requireArguments().getParcelable(ARGS_COUNTER) ?: CounterModel.createNew()
            counterPosition = requireArguments().getInt(ARGS_COUNTER_POSITION, -1)
            isCreateNew = requireArguments().getBoolean(ARGS_CREATE_NEW, false)
        }

        if (savedInstanceState != null) {
            initialColor = savedInstanceState.getInt(STATE_SELECTED_COLOR)
            initialName = savedInstanceState.getString(STATE_SELECTED_NAME)
            initialStartingValue = savedInstanceState.getInt(STATE_SELECTED_STARTING_VALUE)
        } else {
            initialColor = counter.color
            initialName = counter.name
            initialStartingValue = counter.startingAmount
        }
        val builder = AlertDialog.Builder(
            requireActivity(), theme
        )
        builder.setView(view)
            .setTitle(if (isCreateNew) R.string.create_counter_title else R.string.edit_counter_title)
        builder.setNegativeButton(
            R.string.cancel
        ) { _, _ -> dismiss() }
        builder.setPositiveButton(
            R.string.save
        ) { _, _ -> done() }

        //Set this here so the value for getSelectedColor will be accurate immediately upon orientation change
        colorPickerView.setSelectedColor(initialColor)
        counterNameInput.setText(initialName)
        counterNameInput.setInputLimit(10)

        //If creating new token let input show hint of 0 (it will be 0 by default if nothing is input)
        if (!isCreateNew) {
            counterStartingValueInput.setText(initialStartingValue.toString())
        }
        counterStartingValueInput.setInputLimit(4)
        counterStartingValueInput.setOnEditorActionListener(OnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                done()
                return@OnEditorActionListener true
            }
            false
        })
        return builder.create()
    }

    private fun done() {
        val color = colorPickerView.color
        val name = counterNameInput.text.toString()
        val startingValue = selectedStartingValue
        counter = counter.copy(
            color = color,
            name = name,
            startingAmount = startingValue
        )
        if (isCreateNew) {
            counter = counter.copy(
                amount = startingValue
            )
            if (playerListener != null) {
                playerListener!!.onCounterAdded(playerId, counter)
            }
        } else {
            if (playerListener != null) {
                playerListener!!.onCounterEdited(playerId, counterPosition, counter)
            }
        }
        dismiss()
    }

    private val selectedStartingValue: Int
        get() = try {
            counterStartingValueInput.text.toString().toInt()
        } catch (e: NumberFormatException) {
            0
        }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_SELECTED_COLOR, colorPickerView.color)
        outState.putString(STATE_SELECTED_NAME, counterNameInput.text.toString())
        outState.putInt(STATE_SELECTED_STARTING_VALUE, selectedStartingValue)
    }

    companion object {
        private const val ARGS_PLAYER_ID = "args_player_id"
        private const val ARGS_COUNTER = "args_counter"
        private const val ARGS_COUNTER_POSITION = "args_counter_position"
        private const val ARGS_CREATE_NEW = "args_create_new"
        private const val STATE_SELECTED_COLOR = "state_selected_color"
        private const val STATE_SELECTED_NAME = "state_selected_name"
        private const val STATE_SELECTED_STARTING_VALUE = "state_selected_starting_value"
        fun newEditInstance(
            playerId: Int,
            counter: CounterModel,
            counterPosition: Int
        ): EditCounterDialogFragment {
            val f = EditCounterDialogFragment()
            val args = Bundle()
            args.putInt(ARGS_PLAYER_ID, playerId)
            args.putParcelable(ARGS_COUNTER, counter)
            args.putInt(ARGS_COUNTER_POSITION, counterPosition)
            f.arguments = args
            return f
        }

        fun newCreateInstance(playerId: Int): EditCounterDialogFragment {
            val f = EditCounterDialogFragment()
            val args = Bundle()
            args.putInt(ARGS_PLAYER_ID, playerId)
            args.putBoolean(ARGS_CREATE_NEW, true)
            f.arguments = args
            return f
        }
    }
}