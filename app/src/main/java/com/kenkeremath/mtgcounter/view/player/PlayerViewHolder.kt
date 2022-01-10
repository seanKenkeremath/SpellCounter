package com.kenkeremath.mtgcounter.view.player

import android.view.View
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.databinding.ItemPlayerTabletopBinding
import com.kenkeremath.mtgcounter.ui.game.GamePlayerUiModel
import com.kenkeremath.mtgcounter.ui.game.OnPlayerUpdatedListener
import com.kenkeremath.mtgcounter.view.HoldableButton
import com.kenkeremath.mtgcounter.view.PullToRevealLayout
import com.kenkeremath.mtgcounter.view.counter.CountersRecyclerAdapter
import com.kenkeremath.mtgcounter.view.counter.edit.EditCountersRecyclerAdapter
import com.kenkeremath.mtgcounter.view.counter.edit.PlayerMenuListener

/**
 * Generic VH pattern for a player that can be used in a RV or TableTopLayout
 */
class PlayerViewHolder(
    val itemView: View,
    val onPlayerUpdatedListener: OnPlayerUpdatedListener,
    val playerMenuListener: PlayerMenuListener,
) {

    private val binding = ItemPlayerTabletopBinding.bind(itemView)

    private val countersAdapter = CountersRecyclerAdapter(onPlayerUpdatedListener)
    private var playerId: Int = -1

    private val addCountersRecyclerAdapter = EditCountersRecyclerAdapter(playerMenuListener)

    private var layoutResized: Boolean = false
    private var revealHintAnimated: Boolean = false

    private var pullToReveal: Boolean = false
    private var currentMenu: GamePlayerUiModel.Menu? = null

    init {
        binding.countersRecycler.layoutManager =
            LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
        val decoration = DividerItemDecoration(
            itemView.context,
            RecyclerView.HORIZONTAL
        )
        decoration.setDrawable(
            ContextCompat.getDrawable(
                itemView.context,
                R.drawable.divider
            )!!
        )
        binding.countersRecycler.addItemDecoration(
            decoration
        )
        binding.countersRecycler.adapter = countersAdapter

        binding.editCountersRecycler.adapter = addCountersRecyclerAdapter

        binding.addCounter.setOnClickListener {
            playerMenuListener.onEditCountersOpened(playerId)
        }

        binding.revealedAddCounterButton.setListener(object: HoldableButton.HoldableButtonListener {
            override fun onSingleClick() {
                playerMenuListener.onEditCountersOpened(playerId)
            }
            override fun onHoldContinued(increments: Int) {}
        })

        binding.cancel.setOnClickListener {
            playerMenuListener.onCancelCounterChanges(playerId)
            closeEditCounters()
        }

        binding.confirm.setOnClickListener {
            playerMenuListener.onConfirmCounterChanges(playerId)
            closeEditCounters()
        }

        binding.pullToRevealContainer.listener = object : PullToRevealLayout.PullToRevealListener {
            override fun onReveal() {}
            override fun onHide() {
                if (currentMenu == GamePlayerUiModel.Menu.EDIT_COUNTERS) {
                    playerMenuListener.onCancelCounterChanges(playerId)
                }
                playerMenuListener.onCloseSubMenu(playerId)
            }
        }
    }

    private fun closeEditCounters() {
        playerMenuListener.onCloseSubMenu(playerId)
        if (pullToReveal) {
            binding.pullToRevealContainer.hide(true)
        }
    }

    fun bind(data: GamePlayerUiModel) {
        playerId = data.model.id
        countersAdapter.setData(data.model)

        val alphaColor = ColorUtils.setAlphaComponent(
            ContextCompat.getColor(
                itemView.context,
                data.model.colorResId
            ), itemView.resources.getInteger(R.integer.player_color_alpha)
        )

        binding.optionsContainerBgImage.setBackgroundColor(alphaColor)
        binding.playerContainerBgImage.setBackgroundColor(alphaColor)

        // Make usability adjustments based on size (only once measured)
        if (!layoutResized) {
            layoutResized = true
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

                    val minHeightToShowEditCountersHeader =
                        itemView.resources.getDimensionPixelSize(R.dimen.edit_counter_show_header_height_threshold)
                    binding.editCountersHeader.visibility =
                        if (itemView.height < minHeightToShowEditCountersHeader) View.GONE else View.VISIBLE
                    val minHeightToShowMenuText =
                        itemView.resources.getDimensionPixelSize(R.dimen.revealed_menu_show_text_height_threshold)
                    val minWidthToShowMenuText =
                        itemView.resources.getDimensionPixelSize(R.dimen.revealed_menu_show_text_width_threshold)
                    val showMenuText =
                        itemView.width > minWidthToShowMenuText && itemView.height > minHeightToShowMenuText
                    binding.revealedAddCountersLabel.visibility =
                        if (showMenuText) View.VISIBLE else View.GONE
                    binding.revealedRearrangeCountersLabel.visibility =
                        if (showMenuText) View.VISIBLE else View.GONE

                    return false
                }
            })
        }

        pullToReveal = data.pullToReveal
        binding.pullToRevealContainer.setPullEnabled(pullToReveal)
        binding.optionsContainer.visibility = if (pullToReveal) View.GONE else View.VISIBLE

        currentMenu = data.currentMenu
        if (currentMenu == GamePlayerUiModel.Menu.MAIN) {
            binding.editCountersContainer.visibility = View.GONE
            binding.playerContainer.visibility = View.VISIBLE
            binding.revealOptionsMenu.visibility = View.VISIBLE
        } else if (data.currentMenu == GamePlayerUiModel.Menu.EDIT_COUNTERS) {
            binding.playerContainer.visibility = if (pullToReveal) View.VISIBLE else View.GONE
            binding.editCountersContainer.visibility = View.VISIBLE
            binding.revealOptionsMenu.visibility = View.GONE
        }
        if (pullToReveal && !revealHintAnimated) {
            if (!revealHintAnimated) {
                binding.pullToRevealContainer.viewTreeObserver.addOnPreDrawListener(object :
                    ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        binding.pullToRevealContainer.viewTreeObserver.removeOnPreDrawListener(this)
                        binding.pullToRevealContainer.reveal(false)
                        binding.pullToRevealContainer.isEnabled = false
                        binding.pullToRevealContainer.postDelayed(
                            {
                                binding.pullToRevealContainer.isEnabled = true
                                binding.pullToRevealContainer.hide(true)
                            },
                            itemView.resources.getInteger(R.integer.pull_reveal_hint_duration)
                                .toLong()
                        )
                        return false
                    }
                })
                revealHintAnimated = true
            }
        }

        //Scroll to end if there's a new counter, and set ui model flag to false
        if (data.newCounterAdded) {
            binding.countersRecycler.scrollToPosition(countersAdapter.itemCount - 1)
            data.newCounterAdded = false
        }
        addCountersRecyclerAdapter.setCounters(playerId, data.counterSelections)
    }
}