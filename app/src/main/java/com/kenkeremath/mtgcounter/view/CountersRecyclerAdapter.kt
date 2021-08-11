package com.kenkeremath.mtgcounter.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.CounterModel
import com.kenkeremath.mtgcounter.model.PlayerModel
import com.kenkeremath.mtgcounter.ui.game.OnPlayerUpdatedListener

class CountersRecyclerAdapter(
    private val onPlayerUpdatedListener: OnPlayerUpdatedListener
) : RecyclerView.Adapter<CounterViewHolder>() {

    private var player: PlayerModel? = null

    init {
        setHasStableIds(true)
    }

    fun setData(player: PlayerModel) {
        this.player = player
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return "${player!!.counters[position].id}##!##${player?.id}".hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CounterViewHolder {
        return CounterViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_counter, parent, false),
            onPlayerUpdatedListener
        )
    }

    override fun onBindViewHolder(holder: CounterViewHolder, position: Int) {
        holder.bind(player!!.id, player!!.counters[position])
    }

    override fun getItemCount(): Int {
        return player?.counters?.size ?: 0
    }
}

class CounterViewHolder(
    itemView: View,
    onPlayerUpdatedListener: OnPlayerUpdatedListener
) : RecyclerView.ViewHolder(itemView) {
    private var counterView = itemView.findViewById<SecondaryCounterView>(R.id.counter)

    private var playerId: Int = -1
    private var counterId: Int = -1

    init {
        counterView.setOnAmountUpdatedListener(object : CounterView.OnAmountUpdatedListener {
            override fun onAmountSet(amount: Int) {
                onPlayerUpdatedListener.onCounterAmountSet(
                    playerId = playerId,
                    counterId = counterId,
                    amount = amount
                )
            }

            override fun onAmountIncremented(amountDifference: Int) {
                onPlayerUpdatedListener.onCounterIncremented(
                    playerId = playerId,
                    counterId = counterId,
                    amountDifference = amountDifference
                )
            }
        })
    }

    fun bind(playerId: Int, counterModel: CounterModel) {
        this.playerId = playerId
        this.counterId = counterModel.id
        this.counterView.setAmount(counterModel.amount)
    }
}