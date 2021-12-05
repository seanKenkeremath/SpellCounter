package com.kenkeremath.mtgcounter.ui.game.rv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.PlayerModel
import com.kenkeremath.mtgcounter.ui.game.OnPlayerUpdatedListener

class GamePlayerRecyclerAdapter(private val onPlayerUpdatedListener: OnPlayerUpdatedListener) :
    RecyclerView.Adapter<GamePlayerRecyclerViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private val players: MutableList<PlayerModel> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(players: List<PlayerModel>) {
        this.players.clear()
        this.players.addAll(players)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return players[position].id.toLong()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GamePlayerRecyclerViewHolder {
        return GamePlayerRecyclerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_player_list, parent, false),
            onPlayerUpdatedListener
        )
    }

    override fun onBindViewHolder(holder: GamePlayerRecyclerViewHolder, position: Int) {
        holder.bind(players[position])
    }

    override fun getItemCount(): Int {
        return players.size
    }
}