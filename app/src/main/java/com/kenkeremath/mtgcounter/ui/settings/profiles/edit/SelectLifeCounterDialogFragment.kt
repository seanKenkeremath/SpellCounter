package com.kenkeremath.mtgcounter.ui.settings.profiles.edit

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.ui.settings.counters.OnCounterClickedListener
import com.kenkeremath.mtgcounter.ui.settings.profiles.edit.SelectCounterRecyclerAdapter.Companion.ID_DEFAULT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class SelectLifeCounterDialogFragment : DialogFragment(),
    OnCounterClickedListener {

    companion object {
        fun newInstance(
            title: String,
            selectedCounterId: Int?,
            counterOptions: List<CounterTemplateModel>
        ): SelectLifeCounterDialogFragment {
            val args = Bundle()
            args.putParcelableArrayList(ARGS_COUNTERS, ArrayList(counterOptions))
            args.putInt(ARGS_SELECTED_COUNTER_ID, selectedCounterId ?: ID_DEFAULT)
            args.putString(ARGS_TITLE, title)
            val fragment = SelectLifeCounterDialogFragment()
            fragment.arguments = args
            return fragment
        }

        const val TAG = "fragment_select_counter"
        private const val ARGS_TITLE = "args_title"
        private const val ARGS_SELECTED_COUNTER_ID = "args_selected_counter_id"
        private const val ARGS_COUNTERS = "args_counters"
        const val REQUEST_KEY_COUNTER = "request_key_select_counter"
        const val RESULT_COUNTER = "result_select_counter"
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: SelectCounterRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_select_counter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            dismiss()
        }
        arguments?.getString(ARGS_TITLE)?.let {
            toolbar.title = it
        }

        recyclerView = view.findViewById(R.id.counters_recycler)
        recyclerView.layoutManager = GridLayoutManager(
            requireContext(),
            3,
            VERTICAL,
            false
        )
        recyclerAdapter = SelectCounterRecyclerAdapter(
            arguments?.getInt(ARGS_SELECTED_COUNTER_ID) ?: ID_DEFAULT,
            this
        )
        recyclerView.adapter = recyclerAdapter
        recyclerAdapter.setCounters(counters)
    }

    private val counters: List<CounterTemplateModel>
        get() =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getParcelableArrayList(
                    ARGS_COUNTERS,
                    CounterTemplateModel::class.java
                ) ?: emptyList()
            } else {
                arguments?.getParcelableArrayList(
                    ARGS_COUNTERS
                ) ?: emptyList()
            }

    override fun onCounterClicked(id: Int) {
        val counter = if (id == ID_DEFAULT) {
            null
        } else {
            counters.find { it.id == id }
        }
        selectCounter(counter)
    }

    private fun selectCounter(counterTemplateModel: CounterTemplateModel?) {
        val b = Bundle()
        b.putParcelable(RESULT_COUNTER, counterTemplateModel)
        setFragmentResult(REQUEST_KEY_COUNTER, b)
        dismiss()
    }
}