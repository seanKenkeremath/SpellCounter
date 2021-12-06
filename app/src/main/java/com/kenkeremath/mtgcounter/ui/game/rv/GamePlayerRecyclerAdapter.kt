package com.kenkeremath.mtgcounter.ui.game.rv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.ui.game.OnPlayerUpdatedListener
import com.kenkeremath.mtgcounter.ui.game.dagger.GamePlayerUiModel
import com.kenkeremath.mtgcounter.view.counter.edit.OnCounterSelectionListener

class GamePlayerRecyclerAdapter(
    private val onPlayerUpdatedListener: OnPlayerUpdatedListener,
    private val onCounterSelectionListener: OnCounterSelectionListener,
) :
    RecyclerView.Adapter<GamePlayerRecyclerViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private val players: MutableList<GamePlayerUiModel> = mutableListOf()

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
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_player_tabletop, parent, false)
        val lp = v.layoutParams
        lp.height = parent.context.resources.getDimensionPixelSize(R.dimen.player_list_item_height)
        v.layoutParams = lp
        return GamePlayerRecyclerViewHolder(
            v,
            onPlayerUpdatedListener,
            onCounterSelectionListener
        )
    }

    override fun onBindViewHolder(holder: GamePlayerRecyclerViewHolder, position: Int) {
        holder.bind(players[position])
    }

    override fun getItemCount(): Int {
        return players.size
    }
}