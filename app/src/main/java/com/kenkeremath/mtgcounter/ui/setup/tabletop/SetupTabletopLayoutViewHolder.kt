package com.kenkeremath.mtgcounter.ui.setup.tabletop

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.github.rongi.rotate_layout.layout.RotateLayout
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.player.PlayerSetupModel
import com.kenkeremath.mtgcounter.view.TabletopLayoutViewHolder

class SetupTabletopLayoutViewHolder(container: RotateLayout) :
    TabletopLayoutViewHolder<PlayerSetupModel>(container) {

    override val view: View =
        LayoutInflater.from(container.context).inflate(R.layout.item_setup_player, container, false)

    private val colorContainer: View = view.findViewById(R.id.player_setup_color_container)
    private val templateContainer: View = view.findViewById(R.id.player_setup_template_container)
    private val templateName: TextView = view.findViewById(R.id.player_setup_template_name)


    override fun bind(data: PlayerSetupModel) {
        val color = data.colorResId?.let {
            ContextCompat.getColor(view.context, it)
        } ?: Color.TRANSPARENT

        val alphaColor = ColorUtils.setAlphaComponent(
            color, view.resources.getInteger(R.integer.player_color_alpha)
        )

        colorContainer.setBackgroundColor(alphaColor)
        templateName.text = data.template?.name
    }
}