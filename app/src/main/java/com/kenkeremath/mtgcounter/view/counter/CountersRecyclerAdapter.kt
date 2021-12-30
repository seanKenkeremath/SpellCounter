package com.kenkeremath.mtgcounter.view.counter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.counter.CounterModel
import com.kenkeremath.mtgcounter.model.player.PlayerModel
import com.kenkeremath.mtgcounter.ui.game.OnPlayerUpdatedListener

class CountersRecyclerAdapter(
    private val onPlayerUpdatedListener: OnPlayerUpdatedListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_LIFE = 1
        private const val TYPE_COUNTER = 2

        private const val ID_LIFE = "__LIFE__"
    }

    private var player: PlayerModel? = null

    init {
        setHasStableIds(true)
    }

    fun setData(player: PlayerModel) {
        this.player = player
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        val counterId = if (position == 0) ID_LIFE else "${player!!.counters[position - 1].templateId}"
        return "$counterId##!##${player?.id}".hashCode().toLong()
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return TYPE_LIFE
        } else {
            return TYPE_COUNTER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_LIFE) {
            return LifeViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_life_counter, parent, false),
                onPlayerUpdatedListener
            )
        } else {
            return CounterViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_counter, parent, false),
                onPlayerUpdatedListener
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) {
            (holder as LifeViewHolder).bind(
                player!!
            )
        } else {
            (holder as CounterViewHolder).bind(
                player!!.id,
                player!!.counters[position - 1]
            )
        }
    }

    override fun getItemCount(): Int {
        //Life counter is part of this list
        return 1 + (player?.counters?.size ?: 0)
    }
}

class LifeViewHolder(
    itemView: View,
    onPlayerUpdatedListener: OnPlayerUpdatedListener
) : RecyclerView.ViewHolder(itemView) {
    private var lifeView = itemView.findViewById<LifeCounterView>(R.id.counter)

    private var playerId: Int = -1

    init {
        lifeView.setOnAmountUpdatedListener(object : CounterView.OnAmountUpdatedListener {
            override fun onAmountSet(amount: Int) {
                onPlayerUpdatedListener.onLifeAmountSet(
                    playerId = playerId,
                    amount = amount
                )
            }

            override fun onAmountIncremented(amountDifference: Int) {
                onPlayerUpdatedListener.onLifeIncremented(
                    playerId = playerId,
                    amountDifference = amountDifference
                )
            }
        })
    }

    fun bind(playerModel: PlayerModel) {
        this.playerId = playerModel.id
        lifeView.setAmount(playerModel.life)
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
        this.counterId = counterModel.templateId
        this.counterView.setContent(counterModel)
    }
}