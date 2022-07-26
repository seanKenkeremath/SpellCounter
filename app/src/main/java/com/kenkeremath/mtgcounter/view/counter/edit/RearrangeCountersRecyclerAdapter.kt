package com.kenkeremath.mtgcounter.view.counter.edit

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.player.PlayerModel
import com.kenkeremath.mtgcounter.ui.setup.theme.ScThemeUtils
import com.kenkeremath.mtgcounter.view.counter.CounterIconView
import com.kenkeremath.mtgcounter.view.drag.ItemTouchHelperAdapter
import com.kenkeremath.mtgcounter.view.drag.ItemTouchHelperViewHolder
import com.kenkeremath.mtgcounter.view.drag.OnStartDragListener
import java.util.*

class RearrangeCountersRecyclerAdapter(
    val playerMenuListener: PlayerMenuListener,
    val onStartDragListener: OnStartDragListener,
) : RecyclerView.Adapter<RearrangeCounterViewHolder>(), ItemTouchHelperAdapter {

    init {
        setHasStableIds(true)
    }

    private val counters = mutableListOf<RearrangeCounterUiModel>()

    private var player: PlayerModel? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setContent(player: PlayerModel, counters: List<RearrangeCounterUiModel>) {
        this.player = player
        this.counters.clear()
        this.counters.addAll(counters)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return counters[position].template.id.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RearrangeCounterViewHolder {
        return RearrangeCounterViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_rearrange_counter, parent, false),
            onStartDragListener
        )
    }

    override fun onBindViewHolder(holder: RearrangeCounterViewHolder, position: Int) {
        holder.bind(counters[position], player!!)
    }

    override fun getItemCount(): Int {
        return counters.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition == toPosition) {
            return false
        }
        Collections.swap(counters, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {}

    override fun onItemDragFinished(oldPosition: Int, newPosition: Int) {
        playerMenuListener.onCounterRearranged(
            player!!.id,
            counters[oldPosition].template.id,
            oldPosition,
            newPosition
        )
    }
}

@SuppressLint("ClickableViewAccessibility")
class RearrangeCounterViewHolder(itemView: View, onStartDragListener: OnStartDragListener) :
    RecyclerView.ViewHolder(itemView),
    ItemTouchHelperViewHolder {

    private val counterIconView: CounterIconView = itemView.findViewById(R.id.counter_icon_view)

    init {
        itemView.findViewById<View>(R.id.drag_handle).setOnTouchListener { _, _ ->
            onStartDragListener.onStartDrag(this)
            true
        }
        itemView.setOnLongClickListener {
            onStartDragListener.onStartDrag(this)
            true
        }
    }

    fun bind(uiModel: RearrangeCounterUiModel, player: PlayerModel) {
        if (ScThemeUtils.isLightTheme(itemView.context)) {
            counterIconView.setContent(uiModel.template, renderFullArt = true)
        } else {
            counterIconView.setContent(
                uiModel.template,
                renderFullArt = true,
                iconTint = ContextCompat.getColor(itemView.context, player.colorResId)
            )
        }
    }

    override fun onItemSelected() {
        itemView.alpha = .4f
    }

    override fun onItemClear() {
        itemView.alpha = 1f
    }
}