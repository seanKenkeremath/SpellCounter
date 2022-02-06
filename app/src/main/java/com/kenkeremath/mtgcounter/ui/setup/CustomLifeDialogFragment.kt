package com.kenkeremath.mtgcounter.ui.setup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.kenkeremath.mtgcounter.R

class CustomLifeDialogFragment: DialogFragment() {

    companion object {
        fun newInstance(): CustomLifeDialogFragment {
            val args = Bundle()
            val fragment = CustomLifeDialogFragment()
            fragment.arguments = args
            return fragment
        }
        const val TAG = "fragment_custom_life"
        const val REQUEST_CUSTOM_LIFE = "request_custom_life"
        const val RESULT_LIFE_TOTAL = "result_life_total"
    }

    private lateinit var input: EditText
    private lateinit var save: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_custom_life, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        input = view.findViewById(R.id.input_starting_life)
        save = view.findViewById(R.id.save_button)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            dismiss()
        }

        updateButtonState()

        input.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        save.setOnClickListener {
            val life = parseValue()
            life?.let {
                val b = Bundle()
                b.putInt(RESULT_LIFE_TOTAL, it)
                setFragmentResult(REQUEST_CUSTOM_LIFE, b)
                dismiss()
            }
        }
    }

    private fun updateButtonState() {
        save.isEnabled = parseValue() != null
    }

    private fun parseValue(): Int? {
        return try {
            input.text.toString().toIntOrNull()
        } catch (_: Exception) {
            null
        }
    }
}