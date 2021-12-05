package com.kenkeremath.mtgcounter.ui.game.tabletop

import com.github.rongi.rotate_layout.layout.RotateLayout
import com.kenkeremath.mtgcounter.model.PlayerModel
import com.kenkeremath.mtgcounter.ui.dialog.OnGameDialogListener
import com.kenkeremath.mtgcounter.ui.game.OnPlayerUpdatedListener
import com.kenkeremath.mtgcounter.view.TabletopLayout
import com.kenkeremath.mtgcounter.view.TabletopLayoutAdapter

class GameTabletopLayoutAdapter(
    parent: TabletopLayout,
    private val onPlayerUpdatedListener: OnPlayerUpdatedListener,
    private val onGameDialogListener: OnGameDialogListener
) :
    TabletopLayoutAdapter<GameTabletopPlayerViewHolder, PlayerModel>(parent) {

    override fun createViewHolder(container: RotateLayout): GameTabletopPlayerViewHolder {
        return GameTabletopPlayerViewHolder(
            container,
            onPlayerUpdatedListener,
            onGameDialogListener
        )
    }
}