package com.kenkeremath.mtgcounter.ui.game

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.github.rongi.rotate_layout.layout.RotateLayout
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.PlayerModel
import com.kenkeremath.mtgcounter.view.TabletopLayoutViewHolder

class GameTabletopLayoutViewHolder(
    container: RotateLayout,
    private val onPlayerClickedListener: OnPlayerClickedListener
) : TabletopLayoutViewHolder<PlayerModel>(container) {

    override val view: View =
        LayoutInflater.from(container.context).inflate(R.layout.item_player, container, false)

    private val life: TextView = view.findViewById(R.id.life)
    private var playerIndex: Int = -1


    override fun bind(data: PlayerModel) {
        playerIndex = data.id
        life.text = "${data.life}"
        life.setOnClickListener {
            onPlayerClickedListener.onLifeIncremented(playerIndex, 1)
        }
    }

}