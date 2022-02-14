package com.kenkeremath.mtgcounter.ui.game.rv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.ui.game.OnPlayerUpdatedListener
import com.kenkeremath.mtgcounter.ui.game.GamePlayerUiModel
import com.kenkeremath.mtgcounter.view.counter.edit.PlayerMenuListener

class GamePlayerRecyclerAdapter(
    private val onPlayerUpdatedListener: OnPlayerUpdatedListener,
    private val playerMenuListener: PlayerMenuListener,
) :
    RecyclerView.Adapter<GamePlayerRecyclerViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private var recyclerView: RecyclerView? = null

    private var measurementInvalidated = false

    private val players: MutableList<GamePlayerUiModel> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun invalidateMeasurement() {
        measurementInvalidated = true
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(players: List<GamePlayerUiModel>) {
        this.players.clear()
        this.players.addAll(players)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return players[position].model.id.toLong()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GamePlayerRecyclerViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_player_tabletop, parent, false)
        val lp = v.layoutParams
        lp.height = parent.context.resources.getDimensionPixelSize(R.dimen.player_list_item_height)
        v.layoutParams = lp
        return GamePlayerRecyclerViewHolder(
            v,
            onPlayerUpdatedListener,
            playerMenuListener
        )
    }

    override fun onBindViewHolder(holder: GamePlayerRecyclerViewHolder, position: Int) {
        holder.bind(players[position])
        if (recyclerView?.width ?: 0 > 0 && measurementInvalidated) {
            adjustCellHeight(holder)
        } else {
            recyclerView?.viewTreeObserver?.addOnPreDrawListener(object :
                ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    recyclerView?.viewTreeObserver?.removeOnPreDrawListener(this)
                    adjustCellHeight(holder)
                    return false
                }
            })
        }
    }

    private fun adjustCellHeight(holder: RecyclerView.ViewHolder) {
        val resources = holder.itemView.resources
        var playerHeight = resources.getDimensionPixelSize(R.dimen.player_list_item_height)
        val dividerHeight = resources.getDimensionPixelSize(R.dimen.counter_divider_width)
        val dividersTotalHeight = dividerHeight * itemCount
        val totalHeight = playerHeight * itemCount + dividersTotalHeight
        val recyclerViewHeight = recyclerView?.let {
            it.height - it.paddingTop - it.paddingBottom
        } ?: 0

        val scrollThreshold = resources.getDimensionPixelSize(R.dimen.player_scroll_threshold)
        if (totalHeight < recyclerViewHeight || totalHeight - recyclerViewHeight < scrollThreshold) {
            val totalWeight = itemCount
            playerHeight = (1f / totalWeight * (recyclerViewHeight - dividersTotalHeight)).toInt()
        }

        (holder as GamePlayerRecyclerViewHolder).let {
            val lp = it.itemView.layoutParams
            lp.height = playerHeight
            it.itemView.layoutParams = lp
        }
        measurementInvalidated = false
    }

    override fun getItemCount(): Int {
        return players.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }
}