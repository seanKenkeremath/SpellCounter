package com.kenkeremath.mtgcounter.ui.settings.profiles.edit

import android.annotation.SuppressLint
import android.app.ActionBar.LayoutParams
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.ui.settings.counters.OnCounterClickedListener
import com.kenkeremath.mtgcounter.view.counter.CounterIconView


class SelectCounterRecyclerAdapter(
    private val onCounterClickedListener: OnCounterClickedListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private val counters: MutableList<CounterTemplateModel> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setCounters(counters: List<CounterTemplateModel>) {
        this.counters.clear()
        this.counters.addAll(counters)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return SelectCounterViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_counter_selection, parent, false),
            onCounterClickedListener,
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? SelectCounterViewHolder)?.apply {
            bind(counters[position])
        }
    }

    override fun getItemId(position: Int): Long {
        return counters[position].id.toLong()
    }

    override fun getItemCount(): Int {
        return counters.size
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is EditProfileCounterViewHolder) {
            holder.counterIconView.clearImage()
        }
    }
}

class SelectCounterViewHolder(
    itemView: View,
    private val clickListener: OnCounterClickedListener,
) : RecyclerView.ViewHolder(itemView) {

    private val counterIconView = itemView.findViewById<CounterIconView>(R.id.counter_icon_view)
    private var counterId: Int = -1

    init {
        itemView.layoutParams.height =
            itemView.resources.getDimensionPixelSize(R.dimen.edit_counters_row_min_height)
        itemView.layoutParams.width = LayoutParams.MATCH_PARENT
        itemView.setOnClickListener {
            clickListener.onCounterClicked(counterId)
        }
    }

    fun bind(model: CounterTemplateModel) {
        this.counterId = model.id
        counterIconView.setContent(model, renderFullArt = true)
    }
}
