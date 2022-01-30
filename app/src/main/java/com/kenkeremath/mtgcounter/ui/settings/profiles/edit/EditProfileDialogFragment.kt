package com.kenkeremath.mtgcounter.ui.settings.profiles.edit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.model.player.PlayerTemplateModel
import com.kenkeremath.mtgcounter.ui.settings.counters.EditCounterDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileDialogFragment : DialogFragment(), OnEditProfileCounterClickedListener {

    companion object {
        fun newInstance(profile: PlayerTemplateModel? = null): EditProfileDialogFragment {
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
    private val recyclerAdapter = EditProfileRecyclerAdapter(this)

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

        val createCounter: View = view.findViewById(R.id.create_counter)
        createCounter.setOnClickListener {
            val f = EditCounterDialogFragment.newInstance()
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

        val save: View = view.findViewById(R.id.save)
        save.setOnClickListener {
            viewModel.saveChanges()
        }

        recyclerView = view.findViewById(R.id.profile_counters_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = recyclerAdapter

        nameEditText = view.findViewById(R.id.profile_name_edit_text)
        nameEditText.addTextChangedListener(textChangedListener)
        nameEditText.isEnabled = viewModel.isNameChangeEnabled

        viewModel.profileName.observe(viewLifecycleOwner, {
            //Prevent updates while user is typing
            if (!nameEditText.isFocused) {
                //Remove listener while manually setting to avoid loop
                nameEditText.removeTextChangedListener(textChangedListener)
                nameEditText.setText(it)
                nameEditText.addTextChangedListener(textChangedListener)
            }
        })

        viewModel.counterSelections.observe(viewLifecycleOwner, {
            recyclerAdapter.setCounters(it)
        })

        viewModel.saveStatus.observe(viewLifecycleOwner, {
            when (it) {
                SaveProfileResult.SUCCESSFUL -> requireActivity().onBackPressed()
                //TODO
                SaveProfileResult.NAME_CONFLICT -> {}
                SaveProfileResult.NAME_CONFLICT_CANNOT_REPLACE -> {}
                SaveProfileResult.INVALID_NAME -> {}
                else -> {}
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onCounterSelected(id: Int, selected: Boolean) {
        viewModel.selectCounter(id, selected)
    }
}