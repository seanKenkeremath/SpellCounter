package com.kenkeremath.mtgcounter.ui.setup.tabletop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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

        val allColors = CounterColor.values().map {
            ContextCompat.getColor(requireContext(), it.resId ?: R.color.white)
        }.toIntArray()
        val colorMap = mutableMapOf<@ColorInt Int, CounterColor>()
        allColors.forEachIndexed { index, color ->
            colorMap[color] = CounterColor.values()[index]
        }

        binding.colorPickerView.colors = allColors
        binding.colorPickerView.setSelectedColor(
            allColors[CounterColor.values().indexOf(viewModel.setupModel.color)]
        )

        binding.colorPickerView.setOnColorChangedListener {
            colorMap[it]?.let { color ->
                viewModel.updateColor(color)
            }
        }

        viewModel.profiles.observe(viewLifecycleOwner, { profiles ->
            val spinnerOptions = profiles.map {
                it.name
            }
            val spinnerAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                spinnerOptions
            )
            binding.profileSpinner.adapter = spinnerAdapter
            binding.profileSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        viewModel.updateProfile(profiles[position].name)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        })

        binding.saveButton.setOnClickListener {
            val b = Bundle()
            b.putParcelable(RESULT_MODEL, viewModel.setupModel)
            setFragmentResult(REQUEST_CUSTOMIZE, b)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}