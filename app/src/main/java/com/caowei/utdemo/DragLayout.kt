package com.caowei.utdemo

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.customview.widget.ViewDragHelper

class DragLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private lateinit var mDragHelper: ViewDragHelper
    private var mDragOriLeft = 0
    private var mDragOriTop = 0

    init {
        val callback = object : ViewDragHelper.Callback() {
            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                return !mDragHelper.continueSettling(true)
            }

            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
                return top
            }

            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                return left
            }

            override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
                super.onViewCaptured(capturedChild, activePointerId)
                mDragOriLeft = capturedChild.left
                mDragOriTop = capturedChild.top
            }

            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                super.onViewReleased(releasedChild, xvel, yvel)
                val child = getChildAt(0)
                if (child == releasedChild) {
                    mDragHelper.flingCapturedView(
                        paddingLeft,
                        paddingTop,
                        width - paddingRight - child.width,
                        height - paddingBottom - child.height
                    )
                } else {
                    mDragHelper.settleCapturedViewAt(mDragOriLeft, mDragOriTop)
                }
                invalidate()
            }
        }
        mDragHelper = ViewDragHelper.create(this, 1.0f, callback)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return mDragHelper.shouldInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mDragHelper.processTouchEvent(event);
        return true
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mDragHelper.continueSettling(true)) {
            invalidate()
        }
    }
}