package com.kenkeremath.mtgcounter.ui.setup.tabletop

import android.view.LayoutInflater
import android.view.View
import com.github.rongi.rotate_layout.layout.RotateLayout
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.player.PlayerSetupModel
import com.kenkeremath.mtgcounter.view.TabletopLayout
import com.kenkeremath.mtgcounter.view.TabletopLayoutAdapter
import com.kenkeremath.mtgcounter.view.TabletopLayoutViewHolder

class SetupTabletopLayoutAdapter(
    parent: TabletopLayout,
    private val onSetupPlayerSelectedListener: OnSetupPlayerSelectedListener
) :
    TabletopLayoutAdapter<SetupTabletopLayoutViewHolder, PlayerSetupModel>(parent) {
    override fun createViewHolder(container: RotateLayout): SetupTabletopLayoutViewHolder {
        return SetupTabletopLayoutViewHolder(container, onSetupPlayerSelectedListener)
    }
}

class SetupTabletopLayoutViewHolder(
    container: RotateLayout,
    onSetupPlayerSelectedListener: OnSetupPlayerSelectedListener
) : TabletopLayoutViewHolder<PlayerSetupModel>(container) {

    override val view: View =
        LayoutInflater.from(container.context).inflate(R.layout.item_setup_player, container, false)

    private val nestedVh = SetupPlayerViewHolder(view, onSetupPlayerSelectedListener)

    override fun bind(data: PlayerSetupModel) {
        nestedVh.bind(data)
    }
}