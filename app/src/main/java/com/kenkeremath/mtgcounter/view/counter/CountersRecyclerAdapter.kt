package com.kenkeremath.mtgcounter.view.counter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
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

    private var recyclerView: RecyclerView? = null

    init {
        setHasStableIds(true)
    }

    fun setData(player: PlayerModel) {
        this.player = player
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        val counterId =
            if (position == 0) ID_LIFE else "${player!!.counters[position - 1].template.id}"
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
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_life_counter, parent, false),
                onPlayerUpdatedListener
            )
        } else {
            return CounterViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_counter, parent, false),
                onPlayerUpdatedListener
            )
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        if (position == 0) {
            (holder as LifeViewHolder).bind(
                player!!
            )
        } else {
            (holder as CounterViewHolder).bind(
                player!!.id,
                player!!.counters[position - 1],
                player!!
            )
        }

        val isLifeCounter = position == 0
        if (recyclerView?.width ?: 0 > 0) {
            adjustCellWidth(isLifeCounter, holder)
        } else {
            recyclerView?.viewTreeObserver?.addOnPreDrawListener(object :
                ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    recyclerView?.viewTreeObserver?.removeOnPreDrawListener(this)
                    adjustCellWidth(isLifeCounter, holder)
                    return false
                }
            })
        }
    }

    private fun adjustCellWidth(isLifeCounter: Boolean, holder: RecyclerView.ViewHolder) {
        val resources = holder.itemView.resources
        var lifeWidth = resources.getDimensionPixelSize(R.dimen.player_life_width)
        var counterWidth = resources.getDimensionPixelSize(R.dimen.min_counter_width)
        val dividerWidth = resources.getDimensionPixelSize(R.dimen.counter_divider_width)
        val dividersTotalWidth = dividerWidth * (1 + player!!.counters.size)
        val totalWidth = lifeWidth + (player!!.counters.size * counterWidth) + dividersTotalWidth
        val recyclerViewWidth = recyclerView?.let {
            it.width - it.paddingStart - it.paddingEnd
        } ?: 0

        val scrollThreshold = resources.getDimensionPixelSize(R.dimen.counter_scroll_threshold)
        if (totalWidth < recyclerViewWidth || totalWidth - recyclerViewWidth < scrollThreshold) {
            /**
             * Give life view 50% more weight than all other views and stretch them to fit
             * accordingly
             */
            val totalWeight = 1.5f + player!!.counters.size
            lifeWidth = (1.5f / totalWeight * (recyclerViewWidth - dividersTotalWidth)).toInt()
            counterWidth = (1f / totalWeight * (recyclerViewWidth - dividersTotalWidth)).toInt()
        }

        if (isLifeCounter) {
            (holder as LifeViewHolder).let {
                val lp = it.itemView.layoutParams
                lp.width = lifeWidth
                it.itemView.layoutParams = lp
            }
        } else {
            (holder as CounterViewHolder).let {
                val lp = it.itemView.layoutParams
                lp.width = counterWidth
                it.itemView.layoutParams = lp
            }
        }
    }

    override fun getItemCount(): Int {
        //Life counter is part of this list
        return if (player == null) 0 else 1 + (player?.counters?.size ?: 0)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is CounterViewHolder) {
            holder.counterView.clearBackground()
        }
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
        val color = ContextCompat.getColor(itemView.context, playerModel.colorResId)
        lifeView.setCustomCounter(playerModel.lifeCounter, color)
    }
}

class CounterViewHolder(
    itemView: View,
    onPlayerUpdatedListener: OnPlayerUpdatedListener
) : RecyclerView.ViewHolder(itemView) {
    val counterView = itemView.findViewById<SecondaryCounterView>(R.id.counter)

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

    fun bind(playerId: Int, counterModel: CounterModel, playerModel: PlayerModel) {
        this.playerId = playerId
        this.counterId = counterModel.template.id
        counterView.setContent(
            counterModel,
            playerTint = ContextCompat.getColor(itemView.context, playerModel.colorResId)
        )
    }
}