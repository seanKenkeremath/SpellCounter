package com.kenkeremath.mtgcounter.ui.settings.counters.edit

import android.content.Intent
import android.database.Cursor
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
import com.kenkeremath.mtgcounter.util.LogUtils
import dagger.hilt.android.AndroidEntryPoint
import android.provider.MediaStore
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.ui.setup.theme.ScThemeUtils


@AndroidEntryPoint
class EditCounterDialogFragment : DialogFragment() {

    companion object {
        fun newInstance(
            profileName: String? = null,
            template: CounterTemplateModel? = null
        ): EditCounterDialogFragment {
            val args = Bundle()
            args.putString(ARGS_PROFILE_NAME, profileName)
            args.putParcelable(ARGS_COUNTER_TEMPLATE, template)
            val fragment = EditCounterDialogFragment()
            fragment.arguments = args
            return fragment
        }

        //Which profile this counter should be added to. If null, no link is made
        const val ARGS_PROFILE_NAME = "args_profile_name"

        //Which existing counter is being edited. Null means new counter
        const val ARGS_COUNTER_TEMPLATE = "args_counter_template"
        const val TAG = "fragment_edit_counter"
        const val REQUEST_KEY_COUNTER = "request_key_counter"
        const val RESULT_COUNTER = "result_counter"
    }

    private val viewModel: EditCounterViewModel by viewModels()

    private var _binding: FragmentEditCounterBinding? = null
    private val binding get() = _binding!!

    //Use OpenDocument instead of GetContent() so we can get a persistent URI
    private val getImageFileHandler =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                //Make this URI persistent so it can be saved
                requireActivity().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                //Get file name to display
                val fileName: String = if (uri.scheme.equals("file")) {
                    uri.lastPathSegment ?: ""
                } else {
                    var cursor: Cursor? = null
                    try {
                        cursor = requireActivity().contentResolver.query(
                            uri, arrayOf(
                                MediaStore.Images.ImageColumns.DISPLAY_NAME
                            ), null, null, null
                        )
                        if (cursor?.moveToFirst() == true) {
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME))
                        } else {
                            ""
                        }
                    } finally {
                        cursor?.close()
                    }
                }
                viewModel.updateLocalUri(it.toString(), fileName)
            }
        }

    private var debounceUrlInputRunnable: Runnable? = null

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

        binding.toolbar.setNavigationOnClickListener {
            dismiss()
        }

        val previewBackgroundColor = ColorUtils.setAlphaComponent(
            ContextCompat.getColor(
                requireContext(),
                R.color.accent_blue
            ), requireContext().resources.getInteger(R.integer.player_color_alpha)
        )
        if (ScThemeUtils.isLightTheme(requireContext())) {
            binding.counterPreviewView.background = ColorDrawable(previewBackgroundColor)
        }

        val spinnerOptions = CreateCounterType.values().map {
            getString(it.labelResId)
        }.toTypedArray()
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_spinner_text,
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
                debounceUrlInputRunnable?.let {
                    binding.inputCounterUrl.removeCallbacks(it)
                }
                //Debounce to prevent excessive network requests
                debounceUrlInputRunnable = Runnable {
                    LogUtils.d("Updating url input with: ${s.toString()}")
                    viewModel.updateUrl(s.toString())
                }
                binding.inputCounterUrl.postDelayed(debounceUrlInputRunnable, 1500L)
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

        binding.counterLocalImageName.setOnClickListener {
            getImageFileHandler.launch(arrayOf("image/*"))
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

        viewModel.selectedCounterType.observe(viewLifecycleOwner) {
            binding.counterLocalImageContainer.visibility = View.GONE
            binding.counterTextContainer.visibility = View.GONE
            binding.counterUrlContainer.visibility = View.GONE
            binding.counterFullArtContainer.visibility = View.GONE


            //Remove listener while manually setting to avoid loop
            val spinnerListener = binding.counterTypeSpinner.onItemSelectedListener
            binding.counterTypeSpinner.onItemSelectedListener = null
            binding.counterTypeSpinner.setSelection(it.ordinal)
            binding.counterTypeSpinner.onItemSelectedListener = spinnerListener

            when (it) {
                CreateCounterType.TEXT -> {
                    binding.counterTextContainer.visibility = View.VISIBLE
                }
                CreateCounterType.IMAGE -> {
                    binding.counterLocalImageContainer.visibility = View.VISIBLE
                    binding.counterFullArtContainer.visibility = View.VISIBLE
                }
                CreateCounterType.URL -> {
                    binding.counterUrlContainer.visibility = View.VISIBLE
                    binding.counterFullArtContainer.visibility = View.VISIBLE
                }
                else -> {}
            }
        }

        viewModel.counterLabel.observe(viewLifecycleOwner) {
            //Prevent updates while user is typing
            if (!binding.inputCounterText.isFocused) {
                //Remove listener while manually setting to avoid loop
                binding.inputCounterText.removeTextChangedListener(textChangedListener)
                binding.inputCounterText.setText(it)
                binding.inputCounterText.addTextChangedListener(textChangedListener)
            }
        }

        viewModel.counterUrl.observe(viewLifecycleOwner) {
            //Prevent updates while user is typing
            if (!binding.inputCounterUrl.isFocused) {
                //Remove listener while manually setting to avoid loop
                binding.inputCounterUrl.removeTextChangedListener(urlChangedListener)
                binding.inputCounterUrl.setText(it)
                binding.inputCounterUrl.addTextChangedListener(urlChangedListener)
            }
        }

        viewModel.startingValue.observe(viewLifecycleOwner) {
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
        }

        viewModel.resolvedImageLocation.observe(viewLifecycleOwner) {
            binding.counterLocalImageName.text = if (!it.isNullOrBlank()) {
                it
            } else {
                getString(R.string.create_counter_local_uri_hint)
            }
        }

        viewModel.isFullArtImage.observe(viewLifecycleOwner) {
            //Remove listener to prevent loop
            binding.fullArtCheckbox.setOnCheckedChangeListener(null)
            binding.fullArtCheckbox.isChecked = it
            binding.fullArtCheckbox.setOnCheckedChangeListener(fullArtCheckChangedListener)
        }

        viewModel.counterPreview.observe(viewLifecycleOwner) {
            binding.counterPreviewView.apply {
                if (it == null) {
                    visibility = View.INVISIBLE
                } else {
                    visibility = View.VISIBLE
                    setContent(
                        counterModel = it,
                        playerTint = previewBackgroundColor
                    )
                }
            }
        }

        viewModel.saveEnabled.observe(viewLifecycleOwner) {
            binding.saveButton.isEnabled = it
        }
        viewModel.saveStatus.observe(viewLifecycleOwner) {
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
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        debounceUrlInputRunnable?.let {
            binding.inputCounterUrl.removeCallbacks(it)
        }
        _binding = null
    }
}