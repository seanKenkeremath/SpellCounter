package com.kenkeremath.mtgcounter.ui.settings.profiles.edit

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.model.player.PlayerProfileModel
import com.kenkeremath.mtgcounter.ui.settings.counters.edit.EditCounterDialogFragment
import com.kenkeremath.mtgcounter.ui.settings.counters.manage.OnManageCounterClickedListener
import com.kenkeremath.mtgcounter.view.counter.LifeCounterView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class EditProfileDialogFragment : DialogFragment(), OnEditProfileCounterClickedListener,
    OnManageCounterClickedListener {

    companion object {
        fun newInstance(profile: PlayerProfileModel? = null): EditProfileDialogFragment {
            val args = Bundle()
            args.putParcelable(ARGS_PROFILE, profile)
            val fragment = EditProfileDialogFragment()
            fragment.arguments = args
            return fragment
        }

        const val TAG = "fragment_edit_profiles"
        const val ARGS_PROFILE = "args_profile"
    }

    private val viewModel: EditProfileViewModel by viewModels()

    private lateinit var editLifeCounterButton: View
    private lateinit var recyclerView: RecyclerView
    private val recyclerAdapter = EditProfileRecyclerAdapter(this, this)

    private lateinit var nameEditText: EditText
    private val textChangedListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            viewModel.updateName(s.toString())
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        val save: View = view.findViewById(R.id.save)
        save.setOnClickListener {
            viewModel.saveChanges()
        }

        recyclerView = view.findViewById(R.id.profile_counters_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = recyclerAdapter
        val dividers = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.nav_menu_divider)?.let {
            dividers.setDrawable(it)
        }
        recyclerView.addItemDecoration(dividers)

        nameEditText = view.findViewById(R.id.profile_name_edit_text)
        nameEditText.addTextChangedListener(textChangedListener)
        nameEditText.isEnabled = viewModel.isNameChangeEnabled
        nameEditText.isFocusable = viewModel.isNameChangeEnabled

        editLifeCounterButton = view.findViewById(R.id.edit_life_counter_button)
        editLifeCounterButton.setOnClickListener {
            val f = SelectLifeCounterDialogFragment.newInstance(
                getString(R.string.select_life_counter),
                counterOptions = viewModel.availableCounters,
                selectedCounterId = viewModel.lifeCounter.value?.id,
            )
            f.show(childFragmentManager, SelectLifeCounterDialogFragment.TAG)
            f.setFragmentResultListener(
                SelectLifeCounterDialogFragment.REQUEST_KEY_COUNTER
            ) { _, bundle ->
                val selectedCounter =
                    bundle.getParcelable<CounterTemplateModel>(SelectLifeCounterDialogFragment.RESULT_COUNTER)
                viewModel.selectLifeCounter(selectedCounter)
            }
        }

        val previewBackgroundColor = ColorUtils.setAlphaComponent(
            ContextCompat.getColor(
                requireContext(),
                R.color.accent_blue
            ), requireContext().resources.getInteger(R.integer.player_color_alpha)
        )

        val lifeCounterView = view.findViewById<LifeCounterView>(R.id.life_counter_preview_view)
        lifeCounterView.background = ColorDrawable(previewBackgroundColor)

        viewModel.profileName.observe(viewLifecycleOwner) {
            //Prevent updates while user is typing
            if (!nameEditText.isFocused) {
                //Remove listener while manually setting to avoid loop
                nameEditText.removeTextChangedListener(textChangedListener)
                nameEditText.setText(it)
                nameEditText.addTextChangedListener(textChangedListener)
            }
        }

        viewModel.lifeCounter.observe(viewLifecycleOwner) {
            lifeCounterView.setCustomCounter(it)
        }

        viewModel.counterSelections.observe(viewLifecycleOwner) {
            recyclerAdapter.setCounters(it)
        }

        viewModel.saveButtonEnabled.observe(viewLifecycleOwner) {
            save.isEnabled = it
        }

        viewModel.saveStatus.observe(viewLifecycleOwner) {
            when (it) {
                SaveProfileResult.SUCCESSFUL -> requireActivity().finish()
                SaveProfileResult.NAME_CONFLICT -> {
                    val dialog = AlertDialog.Builder(requireContext())
                        .setTitle(R.string.edit_profile_replace_existing_title)
                        .setMessage(
                            getString(
                                R.string.edit_profile_replace_existing_message,
                                viewModel.profileName.value
                            )
                        )
                        .setPositiveButton(R.string.edit_profile_replace_existing_replace) { d, _ ->
                            viewModel.saveChanges(replaceExisting = true)
                            d.dismiss()
                        }
                        .setNegativeButton(android.R.string.cancel) { d, _ ->
                            d.dismiss()
                        }
                    dialog.show()
                }
                SaveProfileResult.NAME_CONFLICT_CANNOT_REPLACE -> {
                    val dialog = AlertDialog.Builder(requireContext())
                        .setTitle(R.string.edit_profile_replace_existing_fail_title)
                        .setMessage(
                            getString(
                                R.string.edit_profile_replace_existing_fail_message,
                                viewModel.profileName.value
                            )
                        )
                        .setPositiveButton(android.R.string.ok) { d, _ ->
                            d.dismiss()
                        }
                    dialog.show()
                }
                //Should be caught be button disabled state
                SaveProfileResult.INVALID_NAME -> {}
                else -> {}
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    fun isBackEnabled(): Boolean {
        return !viewModel.hasEdits
    }

    override fun onCounterSelected(id: Int, selected: Boolean) {
        viewModel.selectCounter(id, selected)
    }

    override fun onCounterClicked(id: Int) {
        //Not used
    }

    override fun onCounterRemoveClicked(id: Int) {
        //Not used
    }

    override fun onCounterCreateClicked() {
        val f = EditCounterDialogFragment.newInstance(viewModel.originalProfileName)
        f.show(childFragmentManager, EditCounterDialogFragment.TAG)
        f.setFragmentResultListener(
            EditCounterDialogFragment.REQUEST_KEY_COUNTER
        ) { _, bundle ->
            val newCounter =
                bundle.getParcelable<CounterTemplateModel>(EditCounterDialogFragment.RESULT_COUNTER)
            newCounter?.let {
                viewModel.addNewCounter(it)
            }
        }
    }
}