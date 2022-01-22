package com.kenkeremath.mtgcounter.view.layoutbutton

import android.view.LayoutInflater
import android.view.View
import com.github.rongi.rotate_layout.layout.RotateLayout
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.view.TabletopLayout
import com.kenkeremath.mtgcounter.view.TabletopLayoutAdapter
import com.kenkeremath.mtgcounter.view.TabletopLayoutViewHolder

class TabletopLayoutButtonAdapter(parent: TabletopLayout) :
    TabletopLayoutAdapter<TabletopLayoutButtonPlayerIconViewHolder, Unit>(parent) {
    override fun createViewHolder(container: RotateLayout): TabletopLayoutButtonPlayerIconViewHolder {
        return TabletopLayoutButtonPlayerIconViewHolder(container)
    }
}

class TabletopLayoutButtonPlayerIconViewHolder(container: RotateLayout) :
    TabletopLayoutViewHolder<Unit>(container) {

    override val view: View =
        LayoutInflater.from(container.context)
            .inflate(R.layout.item_layout_button_player, container, false)

    /**
     * No data to bind
     */
    override fun bind(data: Unit) {}
}