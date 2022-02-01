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
import com.kenkeremath.mtgcounter.view.TabletopLayout
import com.kenkeremath.mtgcounter.view.TabletopLayoutAdapter
import com.kenkeremath.mtgcounter.view.TabletopLayoutViewHolder

class SetupTabletopLayoutAdapter(
    parent: TabletopLayout,
    private val onPlayerSelectedListener: OnPlayerSelectedListener
) :
    TabletopLayoutAdapter<SetupTabletopLayoutViewHolder, PlayerSetupModel>(parent) {
    override fun createViewHolder(container: RotateLayout): SetupTabletopLayoutViewHolder {
        return SetupTabletopLayoutViewHolder(container, onPlayerSelectedListener)
    }
}

class SetupTabletopLayoutViewHolder(
    container: RotateLayout,
    private val onPlayerSelectedListener: OnPlayerSelectedListener
) :
    TabletopLayoutViewHolder<PlayerSetupModel>(container) {

    override val view: View =
        LayoutInflater.from(container.context).inflate(R.layout.item_setup_player, container, false)

    private val playerContainer: View = view.findViewById(R.id.player_container)
    private val templateName: TextView = view.findViewById(R.id.player_setup_template_name)

    private var playerId: Int = -1

    init {
        view.setOnClickListener {
            onPlayerSelectedListener.onPlayerSelected(playerId)
        }
    }

    override fun bind(data: PlayerSetupModel) {
        playerId = data.id
        val color = data.color.resId?.let {
            ContextCompat.getColor(view.context, it)
        } ?: Color.WHITE

        val alphaColor = ColorUtils.setAlphaComponent(
            color, view.resources.getInteger(R.integer.player_color_alpha)
        )

        playerContainer.setBackgroundColor(alphaColor)
        templateName.text = data.template?.name
    }
}