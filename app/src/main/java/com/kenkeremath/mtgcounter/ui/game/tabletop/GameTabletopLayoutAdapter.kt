package com.kenkeremath.mtgcounter.ui.game.tabletop

import com.github.rongi.rotate_layout.layout.RotateLayout
import com.kenkeremath.mtgcounter.ui.game.OnPlayerUpdatedListener
import com.kenkeremath.mtgcounter.ui.game.dagger.GamePlayerUiModel
import com.kenkeremath.mtgcounter.view.TabletopLayout
import com.kenkeremath.mtgcounter.view.TabletopLayoutAdapter
import com.kenkeremath.mtgcounter.view.counter.edit.OnCounterSelectionListener

class GameTabletopLayoutAdapter(
    parent: TabletopLayout,
    private val onPlayerUpdatedListener: OnPlayerUpdatedListener,
    private val onCounterSelectionListener: OnCounterSelectionListener,
) :
    TabletopLayoutAdapter<GameTabletopPlayerViewHolder, GamePlayerUiModel>(parent) {

    override fun createViewHolder(container: RotateLayout): GameTabletopPlayerViewHolder {
        return GameTabletopPlayerViewHolder(
            container,
            onPlayerUpdatedListener,
            onCounterSelectionListener,
        )
    }
}