package com.kenkeremath.mtgcounter.ui.settings.profiles.edit

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.view.counter.CounterIconView
import com.kenkeremath.mtgcounter.view.counter.edit.CounterSelectionUiModel

class EditProfileRecyclerAdapter(private val clickListener: OnEditProfileCounterClickedListener) :
    RecyclerView.Adapter<EditProfileCounterViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private val counters: MutableList<CounterSelectionUiModel> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setCounters(counters: List<CounterSelectionUiModel>) {
        this.counters.clear()
        this.counters.addAll(counters)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EditProfileCounterViewHolder {
        return EditProfileCounterViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_profile_counter, parent, false),
            clickListener
        )
    }

    override fun onBindViewHolder(holder: EditProfileCounterViewHolder, position: Int) {
        holder.bind(counters[position])
    }

    override fun getItemId(position: Int): Long {
        return counters[position].template.id.toLong()
    }

    override fun getItemCount(): Int {
        return counters.size
    }
}

class EditProfileCounterViewHolder(
    itemView: View,
    private val clickListener: OnEditProfileCounterClickedListener,
) : RecyclerView.ViewHolder(itemView), CompoundButton.OnCheckedChangeListener {

    private val counterIconView = itemView.findViewById<CounterIconView>(R.id.counter_icon_view)
    private val counterCheckbox = itemView.findViewById<CheckBox>(R.id.counter_checkbox)

    private var counterId: Int = -1

    init {
        itemView.setOnClickListener {
            clickListener.onCounterSelected(counterId, !itemView.isSelected)
        }
    }

    fun bind(model: CounterSelectionUiModel) {
        this.counterId = model.template.id
        counterIconView.setContent(model.template)
        itemView.isSelected = model.selected

        //Remove listener before setting to avoid loop
        counterCheckbox.setOnCheckedChangeListener(null)
        counterCheckbox.isChecked = model.selected
        counterCheckbox.setOnCheckedChangeListener(this)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        clickListener.onCounterSelected(counterId, isChecked)
    }
}
