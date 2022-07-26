package com.kenkeremath.mtgcounter.ui.settings.profiles.edit

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.ui.settings.counters.manage.CreateCounterViewHolder
import com.kenkeremath.mtgcounter.ui.settings.counters.manage.OnManageCounterClickedListener
import com.kenkeremath.mtgcounter.view.counter.CounterIconView
import com.kenkeremath.mtgcounter.view.counter.edit.CounterSelectionUiModel

class EditProfileRecyclerAdapter(
    private val editProfileCounterClickedListener: OnEditProfileCounterClickedListener,
    private val onManageCounterClickedListener: OnManageCounterClickedListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_CREATE = 0
        private const val TYPE_COUNTER = 1
        private const val ID_CREATE = -1L
    }


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
    ): RecyclerView.ViewHolder {
        return if (viewType == TYPE_CREATE) {
            CreateCounterViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_manage_counter_create, parent, false),
                onManageCounterClickedListener
            )
        } else {
            EditProfileCounterViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_profile_counter, parent, false),
                editProfileCounterClickedListener
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is EditProfileCounterViewHolder) {
            //Offset by one for "Create" button
            holder.bind(counters[position - 1])
        }
    }

    override fun getItemId(position: Int): Long {
        return if (position == 0) {
            ID_CREATE
        } else {
            //Offset by one for "Create" button
            return counters[position - 1].template.id.toLong()
        }
    }

    override fun getItemCount(): Int {
        //add one for the "Create" button
        return counters.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_CREATE else TYPE_COUNTER
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is EditProfileCounterViewHolder) {
            holder.counterIconView.clearImage()
        }
    }
}

class EditProfileCounterViewHolder(
    itemView: View,
    private val clickListener: OnEditProfileCounterClickedListener,
) : RecyclerView.ViewHolder(itemView), CompoundButton.OnCheckedChangeListener {

    val counterIconView = itemView.findViewById<CounterIconView>(R.id.counter_icon_view)
    private val counterCheckbox = itemView.findViewById<CheckBox>(R.id.counter_checkbox)

    private var counterId: Int = -1

    init {
        itemView.setOnClickListener {
            clickListener.onCounterSelected(counterId, !itemView.isSelected)
        }
    }

    fun bind(model: CounterSelectionUiModel) {
        this.counterId = model.template.id
        counterIconView.setContent(model.template, renderFullArt = true)
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
