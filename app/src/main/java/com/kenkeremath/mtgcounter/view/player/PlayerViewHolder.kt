package com.kenkeremath.mtgcounter.view.player

import android.view.View
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.databinding.ItemPlayerTabletopBinding
import com.kenkeremath.mtgcounter.ui.game.OnPlayerUpdatedListener
import com.kenkeremath.mtgcounter.ui.game.dagger.GamePlayerUiModel
import com.kenkeremath.mtgcounter.view.counter.CounterView
import com.kenkeremath.mtgcounter.view.counter.CountersRecyclerAdapter
import com.kenkeremath.mtgcounter.view.counter.edit.EditCountersRecyclerAdapter
import com.kenkeremath.mtgcounter.view.counter.edit.OnCounterSelectionListener

/**
 * Generic VH pattern for a player that can be used in a RV or TableTopLayout
 */
class PlayerViewHolder(
    val itemView: View,
    onPlayerUpdatedListener: OnPlayerUpdatedListener,
    onCounterSelectionListener: OnCounterSelectionListener,
) {

    private val binding = ItemPlayerTabletopBinding.bind(itemView)

    private val countersAdapter = CountersRecyclerAdapter(onPlayerUpdatedListener)
    private var playerId: Int = -1

    private val addCountersRecyclerAdapter = EditCountersRecyclerAdapter(onCounterSelectionListener)

    private var counterRowsResized: Boolean = false

    init {

        binding.countersRecycler.layoutManager =
            LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
        binding.countersRecycler.adapter = countersAdapter

        binding.editCountersRecycler.adapter = addCountersRecyclerAdapter

        binding.life.setOnAmountUpdatedListener(object : CounterView.OnAmountUpdatedListener {
            override fun onAmountSet(amount: Int) {
                binding.life.setAmount(amount)
            }

            override fun onAmountIncremented(amountDifference: Int) {
                onPlayerUpdatedListener.onLifeIncremented(playerId, amountDifference)
            }
        })

        binding.addCounter.setOnClickListener {
            binding.gameContainer.visibility = View.GONE
            binding.editCountersContainer.visibility = View.VISIBLE
        }

        binding.cancel.setOnClickListener {
            onCounterSelectionListener.onCancelCounterChanges(playerId)
            binding.gameContainer.visibility = View.VISIBLE
            binding.editCountersContainer.visibility = View.GONE
        }

        binding.confirm.setOnClickListener {
            onCounterSelectionListener.onConfirmCounterChanges(playerId)
            binding.gameContainer.visibility = View.VISIBLE
            binding.editCountersContainer.visibility = View.GONE
        }
    }

    fun bind(data: GamePlayerUiModel) {
        playerId = data.model.id
        binding.life.setAmount(data.model.life)
        countersAdapter.setData(data.model)
        addCountersRecyclerAdapter.setCounters(playerId, data.counterSelections)

        if (!counterRowsResized) {
            counterRowsResized = true
            itemView.viewTreeObserver.addOnPreDrawListener(object :
                ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    itemView.viewTreeObserver.removeOnPreDrawListener(this)
                    val height = itemView.height
                    val minRowHeight =
                        itemView.resources.getDimensionPixelSize(R.dimen.edit_counters_row_min_height)
                    val rows = height / minRowHeight
                    binding.editCountersRecycler.layoutManager =
                        GridLayoutManager(itemView.context, rows, RecyclerView.HORIZONTAL, false)
                    return false
                }
            })
        }
    }
}