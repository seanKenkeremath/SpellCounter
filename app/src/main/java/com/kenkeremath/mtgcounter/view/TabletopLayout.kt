package com.kenkeremath.mtgcounter.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.rongi.rotate_layout.layout.RotateLayout
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.TabletopType

class TabletopLayout : ConstraintLayout {

    internal val panels : Map<TableLayoutPosition, RotateLayout>


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        LayoutInflater.from(context).inflate(R.layout.view_tabletop, this, true)
        panels = mutableMapOf(
            Pair(TableLayoutPosition.SOLO_PANEL, findViewById(R.id.solo_panel)),
            Pair(TableLayoutPosition.LEFT_PANEL, findViewById(R.id.left_panel)),
            Pair(TableLayoutPosition.RIGHT_PANEL, findViewById(R.id.right_panel)),
            Pair(TableLayoutPosition.TOP_PANEL_1, findViewById(R.id.top_panel_1)),
            Pair(TableLayoutPosition.TOP_PANEL_2, findViewById(R.id.top_panel_2)),
            Pair(TableLayoutPosition.TOP_PANEL_3, findViewById(R.id.top_panel_3)),
            Pair(TableLayoutPosition.BOTTOM_PANEL_1, findViewById(R.id.bottom_panel_1)),
            Pair(TableLayoutPosition.BOTTOM_PANEL_2, findViewById(R.id.bottom_panel_2)),
            Pair(TableLayoutPosition.BOTTOM_PANEL_3, findViewById(R.id.bottom_panel_3))
        )
    }
}

enum class TableLayoutPosition {
    SOLO_PANEL,
    LEFT_PANEL,
    RIGHT_PANEL,
    TOP_PANEL_1,
    TOP_PANEL_2,
    TOP_PANEL_3,
    BOTTOM_PANEL_1,
    BOTTOM_PANEL_2,
    BOTTOM_PANEL_3
}

abstract class TabletopLayoutAdapter<VH, VM>(private val parent: TabletopLayout) where VH : TabletopLayoutViewHolder<VM> {

    private val viewHolders: Map<TableLayoutPosition, VH>

    private val defaultRotations: Map<TableLayoutPosition, Int>

    init {
        viewHolders = mutableMapOf()
        for (tableLayoutPosition in parent.panels.keys) {
            val panel = parent.panels[tableLayoutPosition] ?: continue
            val viewHolder = this.createViewHolder(panel)
            viewHolders[tableLayoutPosition] = viewHolder
            panel.addView(viewHolder.view)
        }
        defaultRotations = mapOf(
            Pair(TableLayoutPosition.SOLO_PANEL, 0),
            Pair(TableLayoutPosition.LEFT_PANEL, 270),
            Pair(TableLayoutPosition.RIGHT_PANEL, 90),
            Pair(TableLayoutPosition.TOP_PANEL_1, 180),
            Pair(TableLayoutPosition.TOP_PANEL_2, 180),
            Pair(TableLayoutPosition.TOP_PANEL_3, 180),
            Pair(TableLayoutPosition.BOTTOM_PANEL_1, 0),
            Pair(TableLayoutPosition.BOTTOM_PANEL_2, 0),
            Pair(TableLayoutPosition.BOTTOM_PANEL_3, 0)
        )

        setDefaultRotations()
    }

    abstract fun createViewHolder(container: RotateLayout): VH

    fun setPositions(tabletopType: TabletopType) {
        for (tableLayoutPosition in TableLayoutPosition.values()) {
            if (!tabletopType.positions.contains(tableLayoutPosition)) {
                updateAtPosition(tableLayoutPosition, null)
            }
        }
    }

    fun updateAtPosition(tableLayoutPosition: TableLayoutPosition, viewModel: VM?) {
        if (viewModel == null) {
            viewHolders[tableLayoutPosition]?.container?.visibility = View.GONE
        } else {
            viewHolders[tableLayoutPosition]?.container?.visibility = View.VISIBLE
            viewHolders[tableLayoutPosition]?.bind(viewModel)
        }
    }

    fun updateAtPosition(tabletopType: TabletopType, playerPosition: Int, viewModel: VM?) {
        updateAtPosition(tabletopType.positions[playerPosition], viewModel)
    }

    fun updateAll(data: Map<TableLayoutPosition, VM>) {
        for (layoutPosition in TableLayoutPosition.values()) {
            updateAtPosition(layoutPosition, data[layoutPosition])
        }
    }

    fun updateAll(type: TabletopType, list: List<VM>) {
        val positionMap : MutableMap<TableLayoutPosition, VM> = mutableMapOf()
        //If empty or mismatched size, all positions will be null/hidden by default
        if (type.numberOfPlayers == list.size) {
            for (i in 0 until list.size) {
                positionMap[type.positions[i]] = list[i]
            }
        }
        updateAll(positionMap)
    }

    //Rotate all to the same absolute rotation value
    fun setRotations(rotation: Int) {
        val rotations : MutableMap<TableLayoutPosition, Int> = mutableMapOf()
        for (tableLayoutPosition in TableLayoutPosition.values()) {
            rotations[tableLayoutPosition] = rotation
        }
        setRotations(rotations)
    }

    //Rotate individual values differently using a map
    fun setRotations(rotations: Map<TableLayoutPosition, Int>) {
        for (layoutPosition in TableLayoutPosition.values()) {
            //Use default rotation value if none specified

            val rotation = rotations[layoutPosition] ?: (defaultRotations[layoutPosition] ?: 0)
            parent.panels[layoutPosition]?.angle = rotation
        }
    }

    //Reset all rotations to default (specified in default rotations map)
    fun setDefaultRotations() {
        //Passing empty map because setRotations uses default values as fallback
        setRotations(emptyMap())
    }
}


abstract class TabletopLayoutViewHolder<VM>(val container: RotateLayout) {
    abstract val view : View
    abstract fun bind(viewModel: VM)
}