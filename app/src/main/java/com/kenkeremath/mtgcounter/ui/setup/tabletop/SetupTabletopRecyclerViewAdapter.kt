package com.kenkeremath.mtgcounter.ui.setup.tabletop

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.player.PlayerSetupModel


class SetupTabletopRecyclerViewAdapter(private val setupPlayerSelectedListener: OnSetupPlayerSelectedListener) :
    RecyclerView.Adapter<PlayerSetupRecyclerViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private var recyclerView: RecyclerView? = null

    private val players: MutableList<PlayerSetupModel> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setPlayers(players: List<PlayerSetupModel>) {
        this.players.clear()
        this.players.addAll(players)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlayerSetupRecyclerViewHolder {
        return PlayerSetupRecyclerViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_setup_player, parent, false),
            setupPlayerSelectedListener
        )
    }

    override fun onBindViewHolder(holder: PlayerSetupRecyclerViewHolder, position: Int) {
        holder.bind(players[position])
        if (recyclerView?.width ?: 0 > 0) {
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

    override fun getItemId(position: Int): Long {
        return players[position].id.toLong()
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

    private fun adjustCellHeight(holder: RecyclerView.ViewHolder) {
        val resources = holder.itemView.resources
        var playerHeight = resources.getDimensionPixelSize(R.dimen.setup_player_list_item_height)
        val totalHeight = playerHeight * itemCount
        val recyclerViewHeight = recyclerView?.let {
            it.height - it.paddingTop - it.paddingBottom
        } ?: 0

        val scrollThreshold = resources.getDimensionPixelSize(R.dimen.player_scroll_threshold)
        if (totalHeight < recyclerViewHeight || totalHeight - recyclerViewHeight < scrollThreshold) {
            val totalWeight = itemCount
            playerHeight = (1f / totalWeight * (recyclerViewHeight)).toInt()
        }

        (holder as PlayerSetupRecyclerViewHolder).let {
            val lp = it.itemView.layoutParams
            lp.height = playerHeight
            it.itemView.layoutParams = lp
        }
    }
}

class PlayerSetupRecyclerViewHolder(
    itemView: View,
    onSetupPlayerSelectedListener: OnSetupPlayerSelectedListener,
) : RecyclerView.ViewHolder(itemView) {

    private val nestedVh = SetupPlayerViewHolder(itemView, onSetupPlayerSelectedListener)

    fun bind(data: PlayerSetupModel) {
        nestedVh.bind(data)
    }
}
