package com.kenkeremath.mtgcounter.ui.game

import com.github.rongi.rotate_layout.layout.RotateLayout
import com.kenkeremath.mtgcounter.model.PlayerModel
import com.kenkeremath.mtgcounter.view.TabletopLayout
import com.kenkeremath.mtgcounter.view.TabletopLayoutAdapter

class GameTabletopLayoutAdapter(parent: TabletopLayout, private val onPlayerClickedListener: OnPlayerClickedListener) :
    TabletopLayoutAdapter<GameTabletopLayoutViewHolder, PlayerModel>(parent) {

    override fun createViewHolder(container: RotateLayout): GameTabletopLayoutViewHolder {
        return GameTabletopLayoutViewHolder(container, onPlayerClickedListener)
    }
}