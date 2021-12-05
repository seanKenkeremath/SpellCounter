package com.kenkeremath.mtgcounter.view.player

import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.PlayerModel
import com.kenkeremath.mtgcounter.ui.dialog.OnGameDialogListener
import com.kenkeremath.mtgcounter.ui.game.OnPlayerUpdatedListener
import com.kenkeremath.mtgcounter.view.CounterView
import com.kenkeremath.mtgcounter.view.CountersRecyclerAdapter

/**
 * Generic VH pattern for a player that can be used in a RV or TableTopLayout
 */
class PlayerViewHolder(
    val itemView: View,
    onPlayerUpdatedListener: OnPlayerUpdatedListener,
    onGameDialogListener: OnGameDialogListener?
) {

    private val life: CounterView = itemView.findViewById(R.id.life)

    //TODO: delete temporary button
    private val addCounter: Button = itemView.findViewById(R.id.add_counter)
    private val countersRecycler: RecyclerView = itemView.findViewById(R.id.counters_recycler)
    private val countersAdapter = CountersRecyclerAdapter(onPlayerUpdatedListener)
    private var playerId: Int = -1

    init {

        countersRecycler.layoutManager =
            LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
        countersRecycler.adapter = countersAdapter

        life.setOnAmountUpdatedListener(object : CounterView.OnAmountUpdatedListener {
            override fun onAmountSet(amount: Int) {
                life.setAmount(amount)
            }

            override fun onAmountIncremented(amountDifference: Int) {
                onPlayerUpdatedListener.onLifeIncremented(playerId, amountDifference)
            }
        })

        addCounter.setOnClickListener {
            onGameDialogListener?.onOpenAddCounterDialog(playerId)
        }
    }

    fun bind(data: PlayerModel) {
        playerId = data.id
        life.setAmount(data.life)
        countersAdapter.setData(data)
    }
}