package com.kenkeremath.mtgcounter.ui.setup.tabletop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.databinding.FragmentSelectPlayerOptionsBinding
import com.kenkeremath.mtgcounter.model.counter.CounterColor
import com.kenkeremath.mtgcounter.model.player.PlayerSetupModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectPlayerOptionsDialogFragment : DialogFragment() {

    companion object {
        fun newInstance(playerSetupModel: PlayerSetupModel): SelectPlayerOptionsDialogFragment {
            val args = Bundle()
            args.putParcelable(ARGS_MODEL, playerSetupModel)
            val fragment = SelectPlayerOptionsDialogFragment()
            fragment.arguments = args
            return fragment
        }

        const val ARGS_MODEL = "args_model"
        const val REQUEST_CUSTOMIZE = "request_customize"
        const val RESULT_MODEL = "result_model"
        const val TAG = "tag_select_player_options_fragment"
    }

    private var _binding: FragmentSelectPlayerOptionsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SelectPlayerOptionsViewModel by viewModels()

    private var spinnerAdapter: SpinnerAdapter? = null

    private val spinnerItemListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            viewModel.profiles.value?.get(position)?.name?.let {
                viewModel.updateProfile(it)
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentSelectPlayerOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            dismiss()
        }

        val allColors = CounterColor.allColors()
        val allColorInts = allColors.map {
            ContextCompat.getColor(requireContext(), it.resId!!)
        }.toIntArray()
        val colorMap = mutableMapOf<@ColorInt Int, CounterColor>()
        allColorInts.forEachIndexed { index, color ->
            colorMap[color] = allColors[index]
        }

        binding.colorPickerView.colors = allColorInts

        binding.colorPickerView.setOnColorChangedListener {
            colorMap[it]?.let { color ->
                viewModel.updateColor(color)
            }
        }

        viewModel.setupModel.observe(viewLifecycleOwner, {
            binding.colorPickerView.setSelectedColor(
                allColorInts[allColors.indexOf(it.color)]
            )
            setSpinnerSelection()
        })

        viewModel.profiles.observe(viewLifecycleOwner, { profiles ->
            val spinnerOptions = profiles.map {
                it.name
            }
            spinnerAdapter = ArrayAdapter(
                requireContext(),
                R.layout.item_spinner_text,
                spinnerOptions
            )
            binding.profileSpinner.adapter = spinnerAdapter
            setSpinnerSelection()
        })

        binding.saveButton.setOnClickListener {
            viewModel.setupModel.value?.let {
                val b = Bundle()
                b.putParcelable(RESULT_MODEL, it)
                setFragmentResult(REQUEST_CUSTOMIZE, b)
                dismiss()
            }
        }
    }

    private fun setSpinnerSelection() {
        binding.profileSpinner.onItemSelectedListener = null
        val index = viewModel.profiles.value?.let { profiles ->
            profiles.indexOfFirst { viewModel.setupModel.value?.template?.name == it.name }
        } ?: -1
        if (index != -1) {
            binding.profileSpinner.setSelection(index)
        }
        binding.profileSpinner.onItemSelectedListener = spinnerItemListener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}