package com.kenkeremath.mtgcounter.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class LockableRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    var scrollingEnabled = true

    override fun computeHorizontalScrollRange(): Int {
        return if (!scrollingEnabled) 0 else super.computeHorizontalScrollRange()
    }

    override fun computeVerticalScrollRange(): Int {
        return if (!scrollingEnabled) 0 else super.computeVerticalScrollRange()
    }

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        return if (!scrollingEnabled) false else super.onInterceptTouchEvent(e)
    }
}