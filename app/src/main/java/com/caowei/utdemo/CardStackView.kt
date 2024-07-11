package com.caowei.utdemo

import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.customview.widget.ViewDragHelper

@RequiresApi(Build.VERSION_CODES.M)
class CardStackView(context: Context) : ViewGroup(context) {
    private val dragHelper: ViewDragHelper
    private val cardViews = mutableListOf<View>()
    private var currentIndex = 0

    init {
        for (i in 0 until 3) {
            val cardView = View(context)
            cardView.setBackgroundColor(resources.getColor(android.R.color.holo_blue_light, null))
            addView(cardView)
            cardViews.add(cardView)
        }
        dragHelper = ViewDragHelper.create(this, 1.0f, DragHelperCallback())
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for ((index, cardView) in cardViews.withIndex()) {
            cardView.layout(
                index * cardView.measuredWidth,
                0,
                (index + 1) * cardView.measuredWidth,
                cardView.measuredHeight
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for (cardView in cardViews) {
            measureChild(cardView, widthMeasureSpec, heightMeasureSpec)
        }
        setMeasuredDimension(
            cardViews.size * cardViews[0].measuredWidth,
            cardViews[0].measuredHeight
        )
    }

    private inner class DragHelperCallback : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return true
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return left
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return top
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            super.onViewReleased(releasedChild, xvel, yvel)
            val targetLeft = currentIndex * releasedChild.width
            dragHelper.settleCapturedViewAt(targetLeft, 0)
            invalidate()
        }
    }

    override fun computeScroll() {
        super.computeScroll()
        if (dragHelper.continueSettling(true)) {
            invalidate()
        }
    }
}