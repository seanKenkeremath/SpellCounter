package com.kenkeremath.mtgcounter.view.counter.edit

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.player.PlayerModel
import com.kenkeremath.mtgcounter.ui.setup.theme.ScThemeUtils
import com.kenkeremath.mtgcounter.view.counter.CounterIconView

class EditCountersRecyclerAdapter(private val playerMenuListener: PlayerMenuListener) :
    RecyclerView.Adapter<CounterSelectionViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private var player: PlayerModel? = null

    private val counters: MutableList<CounterSelectionUiModel> = mutableListOf()

    fun setCounters(player: PlayerModel, counters: List<CounterSelectionUiModel>) {
        this.counters.clear()
        this.counters.addAll(counters)
        this.player = player
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
        player?.let {
            holder.bind(it, counters[position])
        }
    }

    override fun getItemId(position: Int): Long {
        return counters[position].template.id.toLong()
    }

    override fun getItemCount(): Int {
        return counters.size
    }

    override fun onViewRecycled(holder: CounterSelectionViewHolder) {
        super.onViewRecycled(holder)
        holder.iconView.clearImage()
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

    fun bind(player: PlayerModel, model: CounterSelectionUiModel) {
        this.selected = model.selected
        this.templateId = model.template.id
        this.playerId = player.id

        if (ScThemeUtils.isLightTheme(itemView.context)) {
            iconView.setContent(model.template, renderFullArt = true)
        } else {
            iconView.setContent(
                model.template,
                renderFullArt = true,
                iconTint = ContextCompat.getColor(itemView.context, player.colorResId)
            )
        }

        itemView.isSelected = selected
        if (selected) {
            itemView.background = ColorDrawable(
                ScThemeUtils.resolveThemeColor(
                    itemView.context,
                    R.attr.scCounterSelectionColor
                )
            )
        } else {
            itemView.background = null
        }
    }
}
