package com.kenkeremath.mtgcounter.ui.setup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.rongi.rotate_layout.layout.RotateLayout
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.PlayerSetupModel
import com.kenkeremath.mtgcounter.view.TabletopLayoutViewHolder

class SetupTabletopLayoutViewHolder(container: RotateLayout) :
    TabletopLayoutViewHolder<PlayerSetupModel>(container) {

    private val _view: View =
        LayoutInflater.from(container.context).inflate(R.layout.item_setup_player, container, false)
    override val view: View
        get() = _view

    private val colorContainer : View = _view.findViewById(R.id.player_setup_color_container)
    private val templateContainer : View = _view.findViewById(R.id.player_setup_template_container)
    private val templateName : TextView = _view.findViewById(R.id.player_setup_template_name)


    override fun bind(viewModel: PlayerSetupModel) {
        colorContainer.setBackgroundColor(viewModel.color)
        templateName.text = viewModel.template?.name
    }

}