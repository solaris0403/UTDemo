package com.caowei.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ScrollView

class OverScrollView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ScrollView(context, attrs) {
    companion object {
        private const val SCROLL_RATIO = 0.5f
        private const val MAX_SCROLL = 200
    }

    private var innerView: View? = null

    override fun overScrollBy(
        deltaX: Int, deltaY: Int,
        scrollX: Int, scrollY: Int,
        scrollRangeX: Int, scrollRangeY: Int,
        maxOverScrollX: Int, maxOverScrollY: Int,
        isTouchEvent: Boolean
    ): Boolean {
        var newDeltaY = deltaY
        val delta = (deltaY * SCROLL_RATIO).toInt()
        if ((scrollY + deltaY) == 0 || (scrollY - scrollRangeY + deltaY) == 0) {
            newDeltaY = deltaY;  //回弹最后一次滚动，复位
        } else {
            newDeltaY = delta  //增加阻尼效果
        }
        return super.overScrollBy(
            deltaX,
            newDeltaY,
            scrollX,
            scrollY,
            scrollRangeX,
            scrollRangeY,
            maxOverScrollX,
            MAX_SCROLL,
            isTouchEvent
        )
    }
}