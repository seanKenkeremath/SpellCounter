package com.kenkeremath.mtgcounter.ui.settings.counters.manage

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.view.counter.CounterIconView

class ManageCountersRecyclerAdapter(private val clickListener: OnManageProfileClickedListener) :
    RecyclerView.Adapter<ManageCounterViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private val counters: MutableList<ManageCounterUiModel> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setCounters(counters: List<ManageCounterUiModel>) {
        this.counters.clear()
        this.counters.addAll(counters)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ManageCounterViewHolder {
        return ManageCounterViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_manage_counter, parent, false),
            clickListener
        )
    }

    override fun onBindViewHolder(holder: ManageCounterViewHolder, position: Int) {
        holder.bind(counters[position])
    }

    override fun getItemId(position: Int): Long {
        return counters[position].template.id.toLong()
    }

    override fun getItemCount(): Int {
        return counters.size
    }

    override fun onViewRecycled(holder: ManageCounterViewHolder) {
        super.onViewRecycled(holder)
        holder.counterIconView.clearImage()
    }
}

class ManageCounterViewHolder(
    itemView: View,
    private val clickListener: OnManageProfileClickedListener,
) : RecyclerView.ViewHolder(itemView) {

    val counterIconView = itemView.findViewById<CounterIconView>(R.id.counter_icon_view)
    private val counterRemoveButton = itemView.findViewById<ImageView>(R.id.counter_delete)

    private var counterId: Int = -1

    init {
        counterRemoveButton.setOnClickListener {
            clickListener.onCounterRemoveClicked(counterId)
        }
    }

    fun bind(model: ManageCounterUiModel) {
        this.counterId = model.template.id
        counterIconView.setContent(model.template, renderFullArt = true)
    }
}
