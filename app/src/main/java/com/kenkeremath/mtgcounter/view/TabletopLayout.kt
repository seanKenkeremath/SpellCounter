package com.kenkeremath.mtgcounter.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.math.MathUtils.clamp
import com.github.rongi.rotate_layout.layout.RotateLayout
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.util.LogUtils

class TabletopLayout : ConstraintLayout {

    internal val panels: Map<TableLayoutPosition, RotateLayout>

    private val hitRect = Rect()

    private val touchMap = mutableMapOf<Int, View>()
    private val touchTimeMap = mutableMapOf<Int, Long>()

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
            Pair(TableLayoutPosition.TOP_PANEL, findViewById(R.id.top_panel)),
            Pair(TableLayoutPosition.BOTTOM_PANEL, findViewById(R.id.bottom_panel)),
            Pair(TableLayoutPosition.RIGHT_PANEL_1, findViewById(R.id.right_panel_1)),
            Pair(TableLayoutPosition.RIGHT_PANEL_2, findViewById(R.id.right_panel_2)),
            Pair(TableLayoutPosition.RIGHT_PANEL_3, findViewById(R.id.right_panel_3)),
            Pair(TableLayoutPosition.LEFT_PANEL_1, findViewById(R.id.left_panel_1)),
            Pair(TableLayoutPosition.LEFT_PANEL_2, findViewById(R.id.left_panel_2)),
            Pair(TableLayoutPosition.LEFT_PANEL_3, findViewById(R.id.left_panel_3))
        )
        for (key in panels.keys) {
            panels[key]?.tag = key
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return true
    }

    /**
     * To ignore child touch events and prevent delegation entirely (e.g. for display only),
     * set enabled to false
     */

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isEnabled) {
            return super.onTouchEvent(event)
        }
        event?.let {

            if (it.actionMasked != MotionEvent.ACTION_MOVE) {
                val pointerIndex = when (it.actionMasked) {
                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_POINTER_UP -> it.actionIndex
                    else -> 0
                }
                val pointerId = it.getPointerId(pointerIndex)
                val parentX = it.getX(pointerIndex)
                val parentY = it.getY(pointerIndex)
                val childView = getChildForCoordinate(parentX, parentY)

                LogUtils.d(tag = LogUtils.TAG_TABLETOP_TOUCH_EVENTS, message = "touchEvent: x: $parentX, y: $parentY")
                var passedEvent: MotionEvent? = null

                if (childView == null) {
                    return false
                } else if (touchMap.values.contains(childView) && !touchMap.keys.contains(pointerId)) {
                    //Pointer was create an a view with an existing touch event. Ignore
                    return true
                }
                else {
                    val childX = parentX - childView.left
                    val childY = parentY - childView.top

                    var removePointer = false

                    when (it.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            touchMap[pointerId] = childView
                            touchTimeMap[pointerId] = System.currentTimeMillis()
                            LogUtils.d(
                                tag = LogUtils.TAG_TABLETOP_TOUCH_EVENTS,
                                message = "pointerId: $pointerId View tag: ${touchMap[pointerId]?.tag} Touch Started (ACTION_DOWN)"
                            )
                            passedEvent = MotionEvent.obtain(
                                it.downTime,
                                System.currentTimeMillis(),
                                MotionEvent.ACTION_DOWN,
                                childX,
                                childY,
                                0,
                            )
                        }
                        MotionEvent.ACTION_UP -> {
                            if (!touchMap.containsKey(pointerId)) {
                                return true
                            }
                            LogUtils.d(
                                tag = LogUtils.TAG_TABLETOP_TOUCH_EVENTS,
                                message = "pointerId: $pointerId View tag: ${touchMap[pointerId]?.tag} Touch Complete (ACTION_UP)"
                            )
                            passedEvent = MotionEvent.obtain(
                                it.downTime,
                                System.currentTimeMillis(),
                                MotionEvent.ACTION_UP,
                                childX,
                                childY,
                                0,
                            )
                            removePointer = true

                        }
                        MotionEvent.ACTION_POINTER_DOWN -> {
                            touchMap[pointerId] = childView
                            val downTime = System.currentTimeMillis()
                            touchTimeMap[pointerId] = downTime
                            LogUtils.d(
                                tag = LogUtils.TAG_TABLETOP_TOUCH_EVENTS,
                                message = "pointerId: $pointerId View tag: ${touchMap[pointerId]?.tag} Touch Started x: $childX, y: $childY"
                            )
                            passedEvent = MotionEvent.obtain(
                                downTime,
                                System.currentTimeMillis(),
                                MotionEvent.ACTION_DOWN,
                                childX,
                                childY,
                                0,
                            )
                        }
                        MotionEvent.ACTION_POINTER_UP -> {
                            if (!touchMap.containsKey(pointerId)) {
                                return true
                            }
                            LogUtils.d(
                                tag = LogUtils.TAG_TABLETOP_TOUCH_EVENTS,
                                message = "pointerId: $pointerId View tag: ${touchMap[pointerId]?.tag} Touch Complete"
                            )
                            passedEvent = MotionEvent.obtain(
                                touchTimeMap[pointerId]!!,
                                System.currentTimeMillis(),
                                MotionEvent.ACTION_UP,
                                childX,
                                childY,
                                0,
                            )
                            removePointer = true
                        }
                        MotionEvent.ACTION_CANCEL -> {
                            if (!touchMap.containsKey(pointerId)) {
                                return true
                            }
                            LogUtils.d(
                                tag = LogUtils.TAG_TABLETOP_TOUCH_EVENTS,
                                message = "pointerId: $pointerId View tag: ${touchMap[pointerId]?.tag} Touch Complete (Cancel)"
                            )
                            passedEvent = MotionEvent.obtain(
                                touchTimeMap[pointerId]!!,
                                System.currentTimeMillis(),
                                MotionEvent.ACTION_CANCEL,
                                childX,
                                childY,
                                0,
                            )
                            removePointer = true
                        }
                        else -> {}
                    }
                    passedEvent?.let { childEvent ->
                        touchMap[pointerId]?.dispatchTouchEvent(childEvent)
                        childEvent.recycle()
                        if (removePointer) {
                            touchMap.remove(pointerId)
                            touchTimeMap.remove(pointerId)
                        }
                    }
                }
            } else {
                //Make a copy of the keys to avoid concurrent modification
                for (pointerId in touchMap.keys.toList()) {
                    val index = it.findPointerIndex(pointerId)
                    touchMap[pointerId]?.let { child ->
                        val parentX = it.getX(index)
                        val parentY = it.getY(index)
                        val childX = parentX - child.left
                        val childY = parentY - child.top
                        val inBounds = getChildForCoordinate(parentX, parentY) == child
                        if (!inBounds) {
                            LogUtils.d(
                                tag = LogUtils.TAG_TABLETOP_TOUCH_EVENTS,
                                message = "pointerId: $pointerId View tag: ${child.tag} Out of bounds. Touch Complete. x: $childX, y: $childY, left: ${child.left}, top: ${child.top}"
                            )
                            val passedEvent = MotionEvent.obtain(
                                touchTimeMap[pointerId]!!,
                                System.currentTimeMillis(),
                                MotionEvent.ACTION_CANCEL,
                                childX,
                                childY,
                                0,
                            )
                            child.dispatchTouchEvent(passedEvent)
                            passedEvent.recycle()
                            touchMap.remove(pointerId)
                            touchTimeMap.remove(pointerId)
                        } else {
                            LogUtils.d(
                                tag = LogUtils.TAG_TABLETOP_TOUCH_EVENTS,
                                message = "Pointers: ${touchMap.keys} pointerId: $pointerId View tag: ${child.tag} Move: x: $childX, y: $childY"
                            )
                            val passedEvent = MotionEvent.obtain(
                                touchTimeMap[pointerId]!!,
                                System.currentTimeMillis(),
                                MotionEvent.ACTION_MOVE,
                                childX,
                                childY,
                                0,
                            )
                            child.dispatchTouchEvent(passedEvent)
                            passedEvent.recycle()
                        }
                    }
                }
            }
            return true
        }
        return false
    }

    private fun getChildForCoordinate(x: Float, y: Float): View? {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.getHitRect(hitRect)
            if (hitRect.contains(x.toInt(), y.toInt())) {
                return child
            }
        }
        return null
    }
}

enum class TableLayoutPosition {
    SOLO_PANEL,
    TOP_PANEL,
    BOTTOM_PANEL,
    LEFT_PANEL_1,
    LEFT_PANEL_2,
    LEFT_PANEL_3,
    RIGHT_PANEL_1,
    RIGHT_PANEL_2,
    RIGHT_PANEL_3,
}

abstract class TabletopLayoutAdapter<VH, T>(private val parent: TabletopLayout) where VH : TabletopLayoutViewHolder<T> {

    private val viewHolders: MutableMap<TableLayoutPosition, VH> = mutableMapOf()

    private val defaultRotations: Map<TableLayoutPosition, Int> = mapOf(
        Pair(TableLayoutPosition.SOLO_PANEL, 270),
        Pair(TableLayoutPosition.TOP_PANEL, 180),
        Pair(TableLayoutPosition.BOTTOM_PANEL, 0),
        Pair(TableLayoutPosition.LEFT_PANEL_1, 270),
        Pair(TableLayoutPosition.LEFT_PANEL_2, 270),
        Pair(TableLayoutPosition.LEFT_PANEL_3, 270),
        Pair(TableLayoutPosition.RIGHT_PANEL_1, 90),
        Pair(TableLayoutPosition.RIGHT_PANEL_2, 90),
        Pair(TableLayoutPosition.RIGHT_PANEL_3, 90)
    )

    private var currentRotations: Map<TableLayoutPosition, Int> = defaultRotations

    init {
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

    fun updateAtPosition(tableLayoutPosition: TableLayoutPosition, data: T?) {
        if (viewHolders[tableLayoutPosition] == null) {
            val panel = parent.panels[tableLayoutPosition] ?: return
            val viewHolder = this.createViewHolder(panel)
            viewHolders[tableLayoutPosition] = viewHolder
            panel.addView(viewHolder.view)
        }
        if (data == null) {
            viewHolders[tableLayoutPosition]?.container?.visibility = View.GONE
        } else {
            viewHolders[tableLayoutPosition]?.container?.visibility = View.VISIBLE
            viewHolders[tableLayoutPosition]?.bind(data)
        }
    }

    fun updateAtPosition(tabletopType: TabletopType, playerPosition: Int, data: T?) {
        updateAtPosition(tabletopType.positions[playerPosition], data)
    }

    fun updateAll(data: Map<TableLayoutPosition, T>) {
        for (layoutPosition in TableLayoutPosition.values()) {
            updateAtPosition(layoutPosition, data[layoutPosition])
        }
    }

    fun updateAll(type: TabletopType, list: List<T>) {
        val positionMap: MutableMap<TableLayoutPosition, T> = mutableMapOf()
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
        val rotations: MutableMap<TableLayoutPosition, Int> = mutableMapOf()
        for (tableLayoutPosition in TableLayoutPosition.values()) {
            rotations[tableLayoutPosition] = rotation
        }
        setRotations(rotations)
    }

    //Rotate individual values differently using a map
    fun setRotations(rotations: Map<TableLayoutPosition, Int>) {
        currentRotations = rotations
        for (layoutPosition in TableLayoutPosition.values()) {
            //Use default rotation value if none specified
            val rotation = rotations[layoutPosition] ?: (defaultRotations[layoutPosition] ?: 0)
            parent.panels[layoutPosition]?.angle = rotation
        }
    }

    //Reset all rotations to default (specified in default rotations map)
    fun setDefaultRotations() {
        currentRotations = defaultRotations
        setRotations(defaultRotations)
    }

    fun getRotationForPosition(position: TableLayoutPosition): Int {
        return currentRotations[position] ?: defaultRotations[position] ?: 0
    }
}


abstract class TabletopLayoutViewHolder<T>(val container: RotateLayout) {
    abstract val view: View
    abstract fun bind(data: T)
}