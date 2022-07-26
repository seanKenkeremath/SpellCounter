package com.kenkeremath.mtgcounter.view.player

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewTreeObserver
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.*
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.compose.ComposeTheme
import com.kenkeremath.mtgcounter.databinding.ItemPlayerTabletopBinding
import com.kenkeremath.mtgcounter.ui.game.GamePlayerUiModel
import com.kenkeremath.mtgcounter.ui.game.OnPlayerUpdatedListener
import com.kenkeremath.mtgcounter.ui.roll.RollPanel
import com.kenkeremath.mtgcounter.ui.setup.theme.ScThemeUtils
import com.kenkeremath.mtgcounter.view.HoldableButton
import com.kenkeremath.mtgcounter.view.PullToRevealLayout
import com.kenkeremath.mtgcounter.view.counter.CountersRecyclerAdapter
import com.kenkeremath.mtgcounter.view.counter.edit.EditCountersRecyclerAdapter
import com.kenkeremath.mtgcounter.view.counter.edit.PlayerMenuListener
import com.kenkeremath.mtgcounter.view.counter.edit.RearrangeCountersRecyclerAdapter
import com.kenkeremath.mtgcounter.view.drag.OnStartDragListener
import com.kenkeremath.mtgcounter.view.drag.SimpleItemTouchHelperCallback

/**
 * Generic VH pattern for a player that can be used in a RV or TableTopLayout
 */
class PlayerViewHolder(
    val itemView: View,
    val onPlayerUpdatedListener: OnPlayerUpdatedListener,
    val playerMenuListener: PlayerMenuListener,
) : OnStartDragListener {

    private val binding = ItemPlayerTabletopBinding.bind(itemView)

    private var playerId: Int = -1

    private val countersAdapter = CountersRecyclerAdapter(onPlayerUpdatedListener)
    private val editCountersRecyclerAdapter = EditCountersRecyclerAdapter(playerMenuListener)
    private val rearrangeCountersRecyclerAdapter =
        RearrangeCountersRecyclerAdapter(playerMenuListener, this)
    private val rearrangeItemTouchHelper =
        ItemTouchHelper(SimpleItemTouchHelperCallback(rearrangeCountersRecyclerAdapter))

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
                R.drawable.player_divider
            )!!
        )
        binding.countersRecycler.addItemDecoration(
            decoration
        )
        binding.countersRecycler.adapter = countersAdapter
        binding.editCountersRecycler.adapter = editCountersRecyclerAdapter
        binding.rearrangeCountersRecycler.layoutManager =
            LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
        binding.rearrangeCountersRecycler.adapter = rearrangeCountersRecyclerAdapter
        rearrangeItemTouchHelper.attachToRecyclerView(binding.rearrangeCountersRecycler)

        binding.addCounter.setOnClickListener {
            playerMenuListener.onEditCountersOpened(playerId)
        }
        binding.rearrangeCounters.setOnClickListener {
            playerMenuListener.onRearrangeCountersOpened(playerId)
        }
        binding.roll.setOnClickListener {
            playerMenuListener.onRollOpened(playerId)
        }
        binding.rollComposeView.setContent {
            ComposeTheme.ScComposeTheme {
                RollPanel(playerColor = Color.Blue)
            }
        }

        binding.revealedAddCounterButton.setListener(object :
            HoldableButton.HoldableButtonListener {
            override fun onSingleClick() {
                playerMenuListener.onEditCountersOpened(playerId)
            }

            override fun onHoldContinued(increments: Int) {}
        })
        binding.revealedRearrangeCountersButton.setListener(object :
            HoldableButton.HoldableButtonListener {
            override fun onSingleClick() {
                playerMenuListener.onRearrangeCountersOpened(playerId)
            }

            override fun onHoldContinued(increments: Int) {}
        })
        binding.revealedRollButton.setListener(object :
            HoldableButton.HoldableButtonListener {
            override fun onSingleClick() {
                playerMenuListener.onRollOpened(playerId)
            }

            override fun onHoldContinued(increments: Int) {}
        })

        binding.rollDone.setOnClickListener {
            closeCountersSubmenu()
        }

        binding.editCancel.setOnClickListener {
            playerMenuListener.onCancelCounterChanges(playerId)
            closeCountersSubmenu()
        }

        binding.editConfirm.setOnClickListener {
            playerMenuListener.onConfirmCounterChanges(playerId)
            closeCountersSubmenu()
        }

        binding.pullToRevealContainer.listener = object : PullToRevealLayout.PullToRevealListener {
            override fun onReveal() {}
            override fun onHide() {
                playerMenuListener.onCloseSubMenu(playerId)
                binding.rollComposeView.disposeComposition()
                binding.countersRecycler.scrollingEnabled = true
            }

            override fun onDragging() {
                //Prevent RV from capturing drag events
                binding.countersRecycler.scrollingEnabled = false
            }
        }
    }

    private fun closeCountersSubmenu() {
        playerMenuListener.onCloseSubMenu(playerId)
        binding.rollComposeView.disposeComposition()
        if (pullToReveal) {
            binding.pullToRevealContainer.hide(true)
        }
    }

    fun bind(data: GamePlayerUiModel) {
        playerId = data.model.id
        countersAdapter.setData(data.model)

        val color = ContextCompat.getColor(
            itemView.context,
            data.model.colorResId
        )
        val alphaColor = ColorUtils.setAlphaComponent(
            color, itemView.resources.getInteger(R.integer.player_color_alpha)
        )
        val isLightTheme = ScThemeUtils.isLightTheme(itemView.context)
        val bgColor = ScThemeUtils.resolveThemeColor(itemView.context, R.attr.scBackgroundColor)

        if (isLightTheme) {
            binding.optionsContainerBgImage.setBackgroundColor(alphaColor)
            binding.playerContainerBgImage.setBackgroundColor(alphaColor)
        } else {
            val gameButtonColorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_enabled),
                    intArrayOf(-android.R.attr.state_enabled),
                ),
                intArrayOf(
                    color,
                    ScThemeUtils.resolveThemeColor(
                        itemView.context,
                        R.attr.scMenuDisabledButtonColor
                    ),
                )
            )
            binding.optionsContainerBgImage.setBackgroundColor(bgColor)
            binding.playerContainerBgImage.setBackgroundColor(bgColor)
            binding.editCountersHeader.setTextColor(color)
            binding.editCancel.setTextColor(color)
            binding.editConfirm.setTextColor(color)
            binding.revealedRearrangeCountersIcon.imageTintList = gameButtonColorStateList
            binding.revealedRearrangeCountersLabel.setTextColor(gameButtonColorStateList)
            binding.rearrangeCounters.imageTintList = gameButtonColorStateList
            binding.revealedAddCountersIcon.imageTintList = gameButtonColorStateList
            binding.revealedAddCountersLabel.setTextColor(gameButtonColorStateList)
            binding.addCounter.imageTintList = gameButtonColorStateList
            binding.revealedRollIcon.imageTintList = gameButtonColorStateList
            binding.revealedRollLabel.setTextColor(gameButtonColorStateList)
            binding.rollHeader.setTextColor(color)
            binding.roll.imageTintList = gameButtonColorStateList
            binding.rollDone.setTextColor(color)
        }

        // Make usability adjustments based on size (only once measured)
        if (!layoutResized) {
            layoutResized = true
            itemView.viewTreeObserver.addOnPreDrawListener(object :
                ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    itemView.viewTreeObserver.removeOnPreDrawListener(this)
                    val res = itemView.resources
                    val height = itemView.height
                    val width = itemView.width

                    /**
                     * Selection rows
                     */
                    val minRowHeight =
                        itemView.resources.getDimensionPixelSize(R.dimen.edit_counters_row_min_height)
                    val rows = height / minRowHeight
                    binding.editCountersRecycler.layoutManager =
                        GridLayoutManager(itemView.context, rows, RecyclerView.HORIZONTAL, false)

                    /**
                     * Edit Counters Header + Roll Header
                     */
                    val minHeightToShowEditCountersHeader =
                        res.getDimensionPixelSize(R.dimen.edit_counter_show_header_height_threshold)
                    val minHeightForLargeEditCountersHeader =
                        res.getDimensionPixelSize(R.dimen.edit_counter_header_large_text_size_height_threshold)
                    val smallHeaderTextSize =
                        res.getDimension(R.dimen.edit_counter_header_small_text_size)
                    val largeHeaderTextSize =
                        res.getDimension(R.dimen.edit_counter_header_large_text_size)
                    val smallHeaderTextPadding =
                        res.getDimensionPixelSize(R.dimen.edit_counter_header_small_padding)
                    val largeHeaderTextPadding =
                        res.getDimensionPixelSize(R.dimen.edit_counter_header_large_padding)
                    binding.editCountersHeader.visibility =
                        if (height < minHeightToShowEditCountersHeader) View.GONE else View.VISIBLE
                    binding.rollHeader.visibility =
                        if (height < minHeightToShowEditCountersHeader) View.GONE else View.VISIBLE
                    if (height < minHeightForLargeEditCountersHeader) {
                        binding.editCountersHeader.apply {
                            textSize = smallHeaderTextSize
                            setPadding(
                                smallHeaderTextPadding,
                                smallHeaderTextPadding,
                                smallHeaderTextPadding,
                                smallHeaderTextPadding
                            )
                        }
                        binding.rollHeader.apply {
                            textSize = smallHeaderTextSize
                            setPadding(
                                smallHeaderTextPadding,
                                smallHeaderTextPadding,
                                smallHeaderTextPadding,
                                smallHeaderTextPadding
                            )
                        }
                    } else {
                        binding.editCountersHeader.apply {
                            textSize = largeHeaderTextSize
                            setPadding(
                                largeHeaderTextPadding,
                                largeHeaderTextPadding,
                                largeHeaderTextPadding,
                                largeHeaderTextPadding
                            )
                        }
                        binding.rollHeader.apply {
                            textSize = largeHeaderTextSize
                            setPadding(
                                largeHeaderTextPadding,
                                largeHeaderTextPadding,
                                largeHeaderTextPadding,
                                largeHeaderTextPadding
                            )
                        }
                    }

                    /**
                     * Hide roll confirm button to save vertical space in small cells
                     */
                    val minHeightToShowRollConfirm = res.getDimensionPixelSize(R.dimen.roll_confirm_visibility_threshold)
                    if (height < minHeightToShowRollConfirm) {
                        binding.rollDone.visibility = View.GONE
                    } else {
                        binding.rollDone.visibility = View.VISIBLE
                    }

                    /**
                     * Reveal menu text
                     */
                    val minHeightToShowMenuText =
                        res.getDimensionPixelSize(R.dimen.revealed_menu_show_text_height_threshold)
                    val minWidthToShowMenuText =
                        res.getDimensionPixelSize(R.dimen.revealed_menu_show_text_width_threshold)
                    val showMenuText =
                        width > minWidthToShowMenuText && height > minHeightToShowMenuText
                    binding.revealedAddCountersLabel.visibility =
                        if (showMenuText) View.VISIBLE else View.GONE
                    //Rearrange and Roll split the space vertically, do we should split the total height for each calculation
                    binding.revealedRearrangeCountersLabel.visibility =
                        if (showMenuText && height / 2 > minHeightToShowMenuText) View.VISIBLE else View.GONE
                    binding.revealedRollLabel.visibility =
                        if (showMenuText && height / 2 > minHeightToShowMenuText) View.VISIBLE else View.GONE
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
            binding.rollContainer.visibility = View.GONE
        } else if (data.currentMenu == GamePlayerUiModel.Menu.EDIT_COUNTERS) {
            binding.playerContainer.visibility = if (pullToReveal) View.VISIBLE else View.GONE
            binding.editCountersContainer.visibility = View.VISIBLE
            binding.editCountersRecycler.visibility = View.VISIBLE
            binding.rearrangeCountersRecycler.visibility = View.GONE
            binding.editCountersHeader.setText(R.string.edit_counters_title)
            binding.revealOptionsMenu.visibility = View.GONE
            binding.rollContainer.visibility = View.GONE
        } else if (data.currentMenu == GamePlayerUiModel.Menu.REARRANGE_COUNTERS) {
            binding.playerContainer.visibility = if (pullToReveal) View.VISIBLE else View.GONE
            binding.editCountersContainer.visibility = View.VISIBLE
            binding.editCountersRecycler.visibility = View.GONE
            binding.rearrangeCountersRecycler.visibility = View.VISIBLE
            binding.editCountersHeader.setText(R.string.rearrange_counters_title)
            binding.revealOptionsMenu.visibility = View.GONE
            binding.rollContainer.visibility = View.GONE
        } else if (data.currentMenu == GamePlayerUiModel.Menu.ROLL) {
            binding.playerContainer.visibility = if (pullToReveal) View.VISIBLE else View.GONE
            binding.editCountersContainer.visibility = View.GONE
            binding.revealOptionsMenu.visibility = View.GONE
            binding.rollContainer.visibility = View.VISIBLE
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

        binding.rearrangeCounters.isEnabled = data.rearrangeButtonEnabled
        binding.revealedRearrangeCountersButton.isEnabled = data.rearrangeButtonEnabled
        binding.revealedRearrangeCountersLabel.isEnabled = data.rearrangeButtonEnabled
        binding.revealedRearrangeCountersIcon.isEnabled = data.rearrangeButtonEnabled

        //Scroll to end if there's a new counter, and set ui model flag to false
        if (data.newCounterAdded) {
            binding.countersRecycler.scrollToPosition(countersAdapter.itemCount - 1)
            data.newCounterAdded = false
        }
        editCountersRecyclerAdapter.setCounters(data.model, data.counterSelections)
        rearrangeCountersRecyclerAdapter.setContent(data.model, data.rearrangeCounters)
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder?) {
        viewHolder?.let {
            rearrangeItemTouchHelper.startDrag(it)
        }
    }
}