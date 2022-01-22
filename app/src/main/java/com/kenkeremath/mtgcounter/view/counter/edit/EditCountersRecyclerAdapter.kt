package com.kenkeremath.mtgcounter.view.counter.edit

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.view.counter.CounterIconView

class EditCountersRecyclerAdapter(private val playerMenuListener: PlayerMenuListener) :
    RecyclerView.Adapter<CounterSelectionViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private var playerId: Int = -1

    private val counters: MutableList<CounterSelectionUiModel> = mutableListOf()

    fun setCounters(playerId: Int, counters: List<CounterSelectionUiModel>) {
        this.counters.clear()
        this.counters.addAll(counters)
        this.playerId = playerId
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CounterSelectionViewHolder {
        return CounterSelectionViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_counter_selection, parent, false),
            playerMenuListener
        )
    }

    override fun onBindViewHolder(holder: CounterSelectionViewHolder, position: Int) {
        holder.bind(playerId, counters[position])
    }

    override fun getItemId(position: Int): Long {
        return counters[position].template.id.toLong()
    }

    override fun getItemCount(): Int {
        return counters.size
    }
}

class CounterSelectionViewHolder(
    itemView: View,
    playerMenuListener: PlayerMenuListener,
) : RecyclerView.ViewHolder(itemView) {
    val iconView = itemView.findViewById<CounterIconView>(R.id.counter_icon_view)

    private var selected: Boolean = false
    private var templateId: Int = 0
    private var playerId: Int = 0

    init {
        itemView.setOnClickListener {
            if (selected) {
                playerMenuListener.onCounterDeselected(playerId, templateId)
            } else {
                playerMenuListener.onCounterSelected(playerId, templateId)
            }
        }
    }

    fun bind(playerId: Int, model: CounterSelectionUiModel) {
        this.selected = model.selected
        this.templateId = model.template.id
        this.playerId = playerId

        iconView.setContent(model.template)

        itemView.isSelected = selected
        if (selected) {
            itemView.background = ColorDrawable(Color.YELLOW)
        } else {
            itemView.background = null
        }
    }
}
