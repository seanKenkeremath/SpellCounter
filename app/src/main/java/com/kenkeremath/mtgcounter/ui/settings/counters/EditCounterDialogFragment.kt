package com.kenkeremath.mtgcounter.ui.settings.counters

import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.databinding.FragmentEditCounterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditCounterDialogFragment : DialogFragment() {

    companion object {
        //TODO: pass existing template to edit?
        fun newInstance(): EditCounterDialogFragment {
            val args = Bundle()
            val fragment = EditCounterDialogFragment()
            fragment.arguments = args
            return fragment
        }

        const val TAG = "fragment_edit_counter"
        const val REQUEST_KEY_COUNTER = "request_key_counter"
        const val RESULT_COUNTER = "result_counter"
    }

    private val viewModel: EditCounterViewModel by viewModels()

    private var _binding: FragmentEditCounterBinding? = null
    private val binding get() = _binding!!

    private val getImageFileHandler = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        viewModel.updateLocalUri(uri?.toString() ?: "")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentEditCounterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val previewBackgroundColor = ColorUtils.setAlphaComponent(
            ContextCompat.getColor(
                requireContext(),
                R.color.accent_blue
            ), requireContext().resources.getInteger(R.integer.player_color_alpha)
        )
        binding.counterPreviewView.background = ColorDrawable(previewBackgroundColor)

        val spinnerOptions = CreateCounterType.values().map {
            getString(it.labelResId)
        }.toTypedArray()
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            spinnerOptions
        )
        binding.counterTypeSpinner.adapter = spinnerAdapter
        binding.counterTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.selectCounterType(CreateCounterType.values()[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        val textChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.updateLabel(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        val urlChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.updateUrl(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        val startingValueChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.updateStartingValue(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.inputCounterUrl.addTextChangedListener(urlChangedListener)
        binding.inputCounterText.addTextChangedListener(textChangedListener)
        binding.inputCounterStartingValue.addTextChangedListener(startingValueChangedListener)

        binding.counterLocalImageContainer.setOnClickListener {
            getImageFileHandler.launch("image/*")
        }

        val fullArtCheckChangedListener =
            CompoundButton.OnCheckedChangeListener { _, isChecked ->
                viewModel.setIsFullArtImage(
                    isChecked
                )
            }
        binding.fullArtCheckbox.setOnCheckedChangeListener(fullArtCheckChangedListener)

        binding.saveButton.setOnClickListener {
            viewModel.save()
        }

        viewModel.selectedCounterType.observe(viewLifecycleOwner, {
            binding.counterLocalImageContainer.visibility = View.GONE
            binding.counterTextContainer.visibility = View.GONE
            binding.counterUrlContainer.visibility = View.GONE
            when (it) {
                CreateCounterType.TEXT -> {
                    binding.counterTextContainer.visibility = View.VISIBLE
                }
                CreateCounterType.IMAGE -> {
                    binding.counterLocalImageContainer.visibility = View.VISIBLE
                }
                CreateCounterType.URL -> {
                    binding.counterUrlContainer.visibility = View.VISIBLE
                }
                else -> {}
            }
        })

        viewModel.counterLabel.observe(viewLifecycleOwner, {
            //Prevent updates while user is typing
            if (!binding.inputCounterText.isFocused) {
                //Remove listener while manually setting to avoid loop
                binding.inputCounterText.removeTextChangedListener(textChangedListener)
                binding.inputCounterText.setText(it)
                binding.inputCounterText.addTextChangedListener(textChangedListener)
            }
        })

        viewModel.counterUrl.observe(viewLifecycleOwner, {
            //Prevent updates while user is typing
            if (!binding.inputCounterUrl.isFocused) {
                //Remove listener while manually setting to avoid loop
                binding.inputCounterUrl.removeTextChangedListener(urlChangedListener)
                binding.inputCounterUrl.setText(it)
                binding.inputCounterUrl.addTextChangedListener(urlChangedListener)
            }
        })

        viewModel.startingValue.observe(viewLifecycleOwner, {
            //Prevent updates while user is typing
            if (!binding.inputCounterStartingValue.isFocused) {
                //Remove listener while manually setting to avoid loop
                binding.inputCounterStartingValue.removeTextChangedListener(
                    startingValueChangedListener
                )
                binding.inputCounterStartingValue.setText("$it")
                binding.inputCounterStartingValue.addTextChangedListener(
                    startingValueChangedListener
                )
            }
        })

        viewModel.counterImageUri.observe(viewLifecycleOwner, {
            binding.counterLocalImageUri.text = if (!it.isNullOrBlank()) {
                it
            } else {
                getString(R.string.create_counter_local_uri_hint)
            }
        })

        viewModel.isFullArtImage.observe(viewLifecycleOwner, {
            //Remove listener to prevent loop
            binding.fullArtCheckbox.setOnCheckedChangeListener(null)
            binding.fullArtCheckbox.isChecked = it
            binding.fullArtCheckbox.setOnCheckedChangeListener(fullArtCheckChangedListener)
        })

        viewModel.counterPreview.observe(viewLifecycleOwner, {
            if (it == null) {
                binding.counterPreviewView.visibility = View.INVISIBLE
            } else {
                binding.counterPreviewView.visibility = View.VISIBLE
                binding.counterPreviewView.setContent(it)
            }
        })

        viewModel.saveStatus.observe(viewLifecycleOwner, {
            when (it) {
                SaveCounterResult.SUCCESSFUL -> {
                    val b = Bundle()
                    b.putParcelable(RESULT_COUNTER, viewModel.counterTemplate)
                    setFragmentResult(REQUEST_KEY_COUNTER, b)
                    dismiss()
                }
                SaveCounterResult.IMAGE_TOO_LARGE -> {}
                SaveCounterResult.IMAGE_SAVE_FAILED -> {}
                else -> {}
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}