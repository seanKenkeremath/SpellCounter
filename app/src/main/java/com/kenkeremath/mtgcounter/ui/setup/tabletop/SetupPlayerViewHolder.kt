package com.kenkeremath.mtgcounter.ui.setup.tabletop

import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.databinding.ItemSetupPlayerBinding
import com.kenkeremath.mtgcounter.model.player.PlayerSetupModel


/**
 * Generic VH pattern for a setup player that can be used in a RV or TableTopLayout
 */
class SetupPlayerViewHolder(
    val itemView: View,
    private val onSetupPlayerSelectedListener: OnSetupPlayerSelectedListener,
) {

    private val binding = ItemSetupPlayerBinding.bind(itemView)

    private var playerId: Int = -1

    init {
        itemView.setOnClickListener {
            onSetupPlayerSelectedListener.onSetupPlayerSelected(playerId)
        }
    }

    fun bind(data: PlayerSetupModel) {
        playerId = data.id
        val color = data.color.resId?.let {
            ContextCompat.getColor(itemView.context, it)
        } ?: Color.WHITE

        val alphaColor = ColorUtils.setAlphaComponent(
            color, itemView.resources.getInteger(R.integer.player_color_alpha)
        )

        binding.playerBackgroundImage.setBackgroundColor(alphaColor)
        binding.playerSetupTemplateName.text = data.template?.name
    }
}