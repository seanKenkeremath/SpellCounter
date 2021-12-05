package com.kenkeremath.mtgcounter.ui.setup.tabletop

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.github.rongi.rotate_layout.layout.RotateLayout
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.PlayerSetupModel
import com.kenkeremath.mtgcounter.view.TabletopLayoutViewHolder

class SetupTabletopLayoutViewHolder(container: RotateLayout) :
    TabletopLayoutViewHolder<PlayerSetupModel>(container) {

    override val view: View = LayoutInflater.from(container.context).inflate(R.layout.item_setup_player, container, false)

    private val colorContainer : View = view.findViewById(R.id.player_setup_color_container)
    private val templateContainer : View = view.findViewById(R.id.player_setup_template_container)
    private val templateName : TextView = view.findViewById(R.id.player_setup_template_name)


    override fun bind(data: PlayerSetupModel) {
        colorContainer.setBackgroundColor(data.color)
        templateName.text = data.template?.name
    }

}