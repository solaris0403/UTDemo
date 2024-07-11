package com.caowei.swipecard.transformer;

import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.caowei.swipecard.StackLayout;

/**
 * User: fashare(153614131@qq.com)
 * Date: 2017-02-16
 * Time: 22:26
 * <br/>
 * <p>
 * 堆叠效果实现, 默认的 PageTransformer
 * <p>
 * 灵感来源:
 * <a href="http://hukai.me/android-training-course-in-chinese/animations/screen-slide.html">Depth Page Transformer<a/>
 */
public final class StackPageTransformer extends StackLayout.PageTransformer {
    private static final String TAG = "StackPageTransformer";

    private float mMinScale;    // 栈底: 最小页面缩放比
    private float mMaxScale;    // 栈顶: 最大页面缩放比
    private int mStackCount;    // 栈内页面数

    private float mPowBase;     // 基底: 相邻两 page 的大小比例

    /**
     * @param minScale   栈底: 最小页面缩放比
     * @param maxScale   栈顶: 最大页面缩放比
     * @param stackCount 栈内页面数
     */
    public StackPageTransformer(float minScale, float maxScale, int stackCount) {
        mMinScale = minScale;
        mMaxScale = maxScale;
        mStackCount = stackCount;

        if (mMaxScale < mMinScale)
            throw new IllegalArgumentException("The Argument: maxScale must bigger than minScale !");
        mPowBase = (float) Math.pow(mMinScale / mMaxScale, 1.0f / mStackCount);
    }

    public StackPageTransformer() {
//        this(0.8f, 0.95f, 5);
        this(0.8f,1f, 2);
    }

    public final void transformPage(View view, float position, boolean isSwipeLeft) {
        //父容器
        View parent = (View) view.getParent();
        int pageWidth = parent.getMeasuredWidth();
        int pageHeight = parent.getMeasuredHeight();

        //缩放锚点，底部中心
//        view.setPivotX(pageWidth / 2f);
//        view.setPivotY(pageHeight);

        //缩放锚点，右边中心
        view.setPivotX(pageWidth);
        view.setPivotY(pageHeight / 2f);

        //顶部索引
        float bottomPos = mStackCount - 1;

        //禁用点击
        if (view.isClickable()) {
            view.setClickable(false);
        }

        if (position == -1) {
            // [-1]: 完全移出屏幕, 待删除
            view.setVisibility(View.GONE);
        } else if (position < 0) {
            // (-1,0): 拖动中
            view.setVisibility(View.VISIBLE);
            view.setTranslationX(0);
            view.setScaleX(mMaxScale);
            view.setScaleY(mMaxScale);
        } else if (position <= bottomPos) {
            // [0, mStackCount-1]: 堆栈中
            int index = (int) position;  // 整数部分
            //前页面在堆栈中的下一层页面的缩放比例
            float minScale = mMaxScale * (float) Math.pow(mPowBase, index + 1);
            //当前页面在堆栈中的层级的缩放比例
            float maxScale = mMaxScale * (float) Math.pow(mPowBase, index);
            //position 位置的页面的缩放比例。
            float curScale = mMaxScale * (float) Math.pow(mPowBase, position);

            // 从上至下, 调整卡片大小
            float scaleFactor = minScale + (maxScale - minScale) * (1 - Math.abs(position - index));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            view.setVisibility(View.VISIBLE);

            // 从上至下, 调整堆叠位置
//            float offsetY = -pageHeight * (1 - mMaxScale) * (bottomPos - position) / bottomPos;
//            view.setTranslationY(offsetY);
            float offsetX = -pageWidth * (1 - mMaxScale) * (bottomPos - position) / bottomPos;
            offsetX = pageHeight * (1-scaleFactor) / 2;
            view.setTranslationX(offsetX);



            // 只有最上面一张可点击
            if (position == 0) {
                if (!view.isClickable()) {
                    view.setClickable(true);
                }
            }

        } else { // (mStackCount-1, +Infinity]: 待显示(堆栈中展示不下)
            view.setVisibility(View.GONE);
        }
    }
}