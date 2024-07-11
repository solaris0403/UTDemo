package com.caowei.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;

public class StretchScrollView extends HorizontalScrollView {
    // 子View
    private View innerView;
    // 上次手势事件的y坐标
    private float mLastX;
    // 记录子View的正常位置
    private final Rect normal = new Rect();

    public StretchScrollView(@NonNull Context context) {
        super(context);
    }

    public StretchScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StretchScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 去除原本ScrollView滚动到边界时的阴影效果
        setOverScrollMode(OVER_SCROLL_NEVER);
        if (getChildAt(0) != null) {
            innerView = getChildAt(0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 手指松开恢复
                if (!normal.isEmpty()) {
                    planAnimation();
                    normal.setEmpty();
                    if (this.mListener != null) {
                        this.mListener.onStart();
                    }
                }
                mLastX = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                float currentX = ev.getX();
                // 手指刚触及屏幕时，也会触发此事件，此时mLastX的值还是0，会立即触发一个比较大的移动。这里过滤掉这种情况
                LogUtils.eTag("caowei-isNeedTranslate", isNeedTranslate() + "-" + mLastX);
                if (isNeedTranslate() && mLastX != 0) {
                    //保存边界位置
                    if (normal.isEmpty()) {
                        normal.set(innerView.getLeft(), innerView.getTop(), innerView.getRight(), innerView.getBottom());
                    }
                    LogUtils.eTag("caowei-ACTION_MOVE", innerView.getLeft());
                    // 移动布局
                    float distanceX = mLastX - currentX;
                    int offset = (int) (distanceX / 2);
                    innerView.layout(innerView.getLeft() - offset, innerView.getTop(), innerView.getRight() - offset, innerView.getBottom());
                }
                if (!isNeedTranslate() && !normal.isEmpty()) {
                    // 移动布局
                    float distanceX = mLastX - currentX;
                    int offset = (int) (distanceX / 2);
                    innerView.layout(innerView.getLeft() - offset, innerView.getTop(), innerView.getRight() - offset, innerView.getBottom());
                    mLastX = currentX;
                    return true;
                }
                mLastX = currentX;
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 回缩动画
     */
    public void planAnimation() {
//        if (innerView.getLeft() <= normal.left) return;
        // 开启移动动画
//        TranslateAnimation animation = new TranslateAnimation(innerView.getLeft(), normal.left, 0, 0);
//        animation.setDuration(200);
//        innerView.startAnimation(animation);
//        // 设置回到正常的布局位置
//        innerView.layout(normal.left, normal.top, normal.right, normal.bottom);
    }

    /**
     * 是否需要Y移动布局
     */
    public boolean isNeedTranslate() {
        int offset = innerView.getMeasuredWidth() - getWidth();
        int scrollX = getScrollX();
        // 顶部或者底部
//        return scrollX == 0 || scrollX == offset;
        // 底部
        return scrollX == offset;
    }

    private OnOverScrollListener mListener;

    public interface OnOverScrollListener {
        void onStart();
    }

    public void setOnOverScrollListener(OnOverScrollListener listener) {
        this.mListener = listener;
    }
}
