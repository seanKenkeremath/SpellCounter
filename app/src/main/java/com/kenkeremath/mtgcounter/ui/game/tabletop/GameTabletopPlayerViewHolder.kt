package com.kenkeremath.mtgcounter.ui.game.tabletop

import android.view.LayoutInflater
import android.view.View
import com.github.rongi.rotate_layout.layout.RotateLayout
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.ui.game.OnPlayerUpdatedListener
import com.kenkeremath.mtgcounter.ui.game.GamePlayerUiModel
import com.kenkeremath.mtgcounter.view.TabletopLayoutViewHolder
import com.kenkeremath.mtgcounter.view.counter.edit.OnCounterSelectionListener
import com.kenkeremath.mtgcounter.view.player.PlayerViewHolder

class GameTabletopPlayerViewHolder(
    container: RotateLayout,
    onPlayerUpdatedListener: OnPlayerUpdatedListener,
    onCounterSelectionListener: OnCounterSelectionListener,
) : TabletopLayoutViewHolder<GamePlayerUiModel>(container) {

    private val nestedPlayerVH = PlayerViewHolder(
        LayoutInflater.from(container.context)
            .inflate(R.layout.item_player_tabletop, container, false),
        onPlayerUpdatedListener,
        onCounterSelectionListener,
    )

    override fun bind(data: GamePlayerUiModel) {
        nestedPlayerVH.bind(data)
    }

    override val view: View
        get() = nestedPlayerVH.itemView
}