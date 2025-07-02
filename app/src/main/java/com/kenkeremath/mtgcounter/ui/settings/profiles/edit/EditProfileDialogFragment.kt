package com.kenkeremath.mtgcounter.ui.settings.profiles.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
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

    private lateinit var recyclerView: RecyclerView
    private val contentAdapter = EditProfileContentAdapter(
        editProfileCounterClickedListener = this,
        onManageCounterClickedListener = this,
        onNameChangedListener = { name -> viewModel.updateName(name) },
        onEditLifeCounterClickListener = { showLifeCounterDialog() }
    )


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

        recyclerView = view.findViewById(R.id.profile_content_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = contentAdapter

        viewModel.profileName.observe(viewLifecycleOwner) {
            updateContent()
        }

        viewModel.lifeCounter.observe(viewLifecycleOwner) {
            updateContent()
        }

        viewModel.counterSelections.observe(viewLifecycleOwner) {
            updateContent()
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

    private fun updateContent() {
        contentAdapter.setItems(
            profileName = viewModel.profileName.value ?: "",
            isNameChangeEnabled = viewModel.isNameChangeEnabled,
            lifeCounter = viewModel.lifeCounter.value,
            counterSelections = viewModel.counterSelections.value ?: emptyList()
        )
    }

    private fun showLifeCounterDialog() {
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
}