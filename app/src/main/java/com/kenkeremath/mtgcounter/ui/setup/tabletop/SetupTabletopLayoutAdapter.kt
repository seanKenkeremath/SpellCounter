package com.kenkeremath.mtgcounter.ui.setup.tabletop

import com.github.rongi.rotate_layout.layout.RotateLayout
import com.kenkeremath.mtgcounter.model.PlayerSetupModel
import com.kenkeremath.mtgcounter.view.TabletopLayout
import com.kenkeremath.mtgcounter.view.TabletopLayoutAdapter

class SetupTabletopLayoutAdapter(parent: TabletopLayout) :
    TabletopLayoutAdapter<SetupTabletopLayoutViewHolder, PlayerSetupModel>(parent) {
    override fun createViewHolder(container: RotateLayout): SetupTabletopLayoutViewHolder {
        return SetupTabletopLayoutViewHolder(container)
    }
}