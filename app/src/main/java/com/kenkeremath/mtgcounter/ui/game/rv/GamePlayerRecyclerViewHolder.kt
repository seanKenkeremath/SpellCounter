package com.kenkeremath.mtgcounter.ui.game.rv

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.ui.game.OnPlayerUpdatedListener
import com.kenkeremath.mtgcounter.ui.game.GamePlayerUiModel
import com.kenkeremath.mtgcounter.view.counter.edit.PlayerMenuListener
import com.kenkeremath.mtgcounter.view.player.PlayerViewHolder

class GamePlayerRecyclerViewHolder(
    itemView: View,
    onPlayerUpdatedListener: OnPlayerUpdatedListener,
    playerMenuListener: PlayerMenuListener,
) : RecyclerView.ViewHolder(itemView) {

    private val wrappedTableTopVH =
        PlayerViewHolder(itemView, onPlayerUpdatedListener, playerMenuListener)

    fun bind(data: GamePlayerUiModel) {
        wrappedTableTopVH.bind(data)
    }
}