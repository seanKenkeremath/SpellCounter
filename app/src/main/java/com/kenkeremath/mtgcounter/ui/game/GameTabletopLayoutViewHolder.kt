package com.kenkeremath.mtgcounter.ui.game

import android.view.LayoutInflater
import android.view.View
import com.github.rongi.rotate_layout.layout.RotateLayout
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.PlayerModel
import com.kenkeremath.mtgcounter.view.CounterView
import com.kenkeremath.mtgcounter.view.TabletopLayoutViewHolder

class GameTabletopLayoutViewHolder(
    container: RotateLayout,
    private val onPlayerClickedListener: OnPlayerClickedListener
) : TabletopLayoutViewHolder<PlayerModel>(container) {

    override val view: View =
        LayoutInflater.from(container.context).inflate(R.layout.item_player, container, false)

    private val life: CounterView = view.findViewById(R.id.life)
    private var playerIndex: Int = -1


    init {
        life.setOnAmountUpdatedListener(object: CounterView.OnAmountUpdatedListener {
            override fun onAmountSet(amount: Int) {
                life.setAmount(amount)
            }
            override fun onAmountIncremented(amountDifference: Int) {
                onPlayerClickedListener.onLifeIncremented(playerIndex, amountDifference)
            }
        })
    }


    override fun bind(data: PlayerModel) {
        playerIndex = data.id
        life.setAmount(data.life)
    }
}