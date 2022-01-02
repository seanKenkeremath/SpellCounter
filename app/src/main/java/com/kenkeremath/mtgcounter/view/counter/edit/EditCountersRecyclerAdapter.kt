package com.kenkeremath.mtgcounter.view.counter.edit

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R

class EditCountersRecyclerAdapter(private val onCounterSelectionListener: OnCounterSelectionListener) :
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
            onCounterSelectionListener
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
    onCounterSelectionListener: OnCounterSelectionListener,
) : RecyclerView.ViewHolder(itemView) {
    val image = itemView.findViewById<ImageView>(R.id.counter_image)
    val text = itemView.findViewById<TextView>(R.id.counter_text)

    private var selected: Boolean = false
    private var templateId: Int = 0
    private var playerId: Int = 0

    init {
        itemView.setOnClickListener {
            if (selected) {
                onCounterSelectionListener.onCounterDeselected(playerId, templateId)
            } else {
                onCounterSelectionListener.onCounterSelected(playerId, templateId)
            }
        }
    }

    fun bind(playerId: Int, model: CounterSelectionUiModel) {
        this.selected = model.selected
        this.templateId = model.template.id
        this.playerId = playerId

        text.visibility = if (model.template.name.isNullOrBlank()) View.GONE else View.VISIBLE
        text.text = model.template.name
        //TODO URI

        if (model.template.symbol.resId == null) {
            if (model.template.color.resId != null) {
                image.setImageResource(R.drawable.ic_circle)
                val tintColor = ContextCompat.getColor(itemView.context, model.template.color.resId!!)
                image.imageTintList = ColorStateList.valueOf(tintColor)
            } else {
                image.setImageDrawable(null)
            }
        } else {
            image.setImageResource(model.template.symbol.resId!!)
            image.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.default_icon_tint))
        }
        itemView.isSelected = selected
        if (selected) {
            itemView.background = ColorDrawable(Color.YELLOW)
        } else {
            itemView.background = null
        }
    }
}
