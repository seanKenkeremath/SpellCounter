package com.kenkeremath.mtgcounter.ui.game

import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.rongi.rotate_layout.layout.RotateLayout
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.PlayerModel
import com.kenkeremath.mtgcounter.view.CounterView
import com.kenkeremath.mtgcounter.view.CountersRecyclerAdapter
import com.kenkeremath.mtgcounter.view.TabletopLayoutViewHolder

class GameTabletopLayoutViewHolder(
    container: RotateLayout,
    private val onPlayerUpdatedListener: OnPlayerUpdatedListener
) : TabletopLayoutViewHolder<PlayerModel>(container) {

    override val view: View =
        LayoutInflater.from(container.context).inflate(R.layout.item_player, container, false)

    private val life: CounterView = view.findViewById(R.id.life)
    //TODO: delete temporary button
    private val addCounter: Button = view.findViewById(R.id.add_counter)
    private val countersRecycler: RecyclerView = view.findViewById(R.id.counters_recycler)
    private val countersAdapter = CountersRecyclerAdapter(onPlayerUpdatedListener)
    private var playerId: Int = -1

    init {

        countersRecycler.layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
        countersRecycler.adapter = countersAdapter

        life.setOnAmountUpdatedListener(object: CounterView.OnAmountUpdatedListener {
            override fun onAmountSet(amount: Int) {
                life.setAmount(amount)
            }
            override fun onAmountIncremented(amountDifference: Int) {
                onPlayerUpdatedListener.onLifeIncremented(playerId, amountDifference)
            }
        })

        //TODO: remove
        addCounter.setOnClickListener {
            onPlayerUpdatedListener.onCounterAdded(playerId)
        }
    }

    override fun bind(data: PlayerModel) {
        playerId = data.id
        life.setAmount(data.life)
        countersAdapter.setData(data)
    }
}