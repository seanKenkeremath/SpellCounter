package com.kenkeremath.mtgcounter.ui.game.rv

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.model.PlayerModel
import com.kenkeremath.mtgcounter.ui.dialog.OnGameDialogListener
import com.kenkeremath.mtgcounter.ui.game.OnPlayerUpdatedListener
import com.kenkeremath.mtgcounter.view.player.PlayerViewHolder

class GamePlayerRecyclerViewHolder(
    itemView: View,
    onPlayerUpdatedListener: OnPlayerUpdatedListener,
    onGameDialogListener: OnGameDialogListener,
) : RecyclerView.ViewHolder(itemView) {

    private val wrappedTableTopVH =
        PlayerViewHolder(itemView, onPlayerUpdatedListener, onGameDialogListener)

    fun bind(data: PlayerModel) {
        wrappedTableTopVH.bind(data)
    }
}