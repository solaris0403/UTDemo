package com.caowei.utdemo;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.util.Pools;

import java.util.Arrays;

public class PathView extends FrameLayout {

    public PathView(Context context) {
        super(context);
        initView();
    }

    public PathView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public PathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        LinearGradient linearGradient = new LinearGradient(getWidth(), 400, 0, 0, Color.RED, Color.GREEN, Shader.TileMode.CLAMP);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setAntiAlias(true);
//        //线条宽度
//        mPaint.setStrokeWidth(10);
//        mPaint.setColor(Color.RED);
//        //设置起始点的位置为(200,400)
//        mPath.moveTo(200, 400);
//        //线条p0-p2控制点(300,300) 终点位置(400,400)
//        mPath.quadTo(300, 300, 400, 400);
//        //线条p2-p4控制点(500,500) 终点位置(600,400)
////        mPath.quadTo(500,500,600,400);
//        canvas.drawPath(mPath, mPaint);
//        canvas.drawCircle(200, 200, 150, mPaint);
//        canvas.drawPoint(100,100, mPaint);
//        float[] pts = {50,50,100,200,300,200,200,400};
//        canvas.drawPoints(pts,0,8, mPaint);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int height = getMeasuredHeight();
        float start = height * 0.25f;
        float end = height * 0.75f;

        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setDuration(TIME);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                PointF bezierPoint = BezierUtils.getBezierPoint(value,
                        new PointF(100, start - 15),
                        new PointF(200, start + 0),
                        new PointF(300, start + 15),
//                        new PointF(400, start + 15),
                        new PointF(500, start + 0),
                        new PointF(600, start - 15)
                );
                start(bezierPoint.y, 0);
                start(bezierPoint.y + 10, 1);
                start(bezierPoint.y + 20, 2);
                start(bezierPoint.y + 30, 3);
                start(bezierPoint.y + 40, 4);
                start(bezierPoint.y + 50, 5);
                start(bezierPoint.y + 60, 6);
            }
        });
        animator.start();
    }

    private static final Pools.SimplePool<TrailingView> sPool = new Pools.SimplePool<TrailingView>(200);


    /**
     * 获取X轴随机点
     *
     * @param y 轴位置
     */
    public PointF getRandomP(float y) {
        return new PointF((float) Math.random() * getMeasuredWidth(), y);
    }

    private BezierAnimUpdateListener listener;

    private final int[] colors = new int[]{
            Color.parseColor("#FF0000"),
            Color.parseColor("#FF7F00"),
            Color.parseColor("#FFFF00"),
            Color.parseColor("#00FF00"),
            Color.parseColor("#00FFFF"),
            Color.parseColor("#0000FF"),
            Color.parseColor("#8B00FF"),
    };

    private static final int TIME = 1100;

    public void start(float y, int index) {
        TrailingView trailingView = sPool.acquire();
        if (trailingView == null) {
            trailingView = new TrailingView(getContext());
        }
//        int index = (int) (Math.random() * 10 * colors.length);
        trailingView.setColor(colors[index]);
        trailingView.setY(y);
        addView(trailingView);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(trailingView, "translationX", getWidth() / 2.0f, getWidth());
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.addListener(new TrailingViewListener(trailingView));
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.addUpdateListener(new BezierAnimUpdateListener(trailingView, getWidth() / 2.0f, getWidth()));
        objectAnimator.setDuration(TIME);
        objectAnimator.start();
    }

    class TrailingViewListener implements Animator.AnimatorListener {
        private final TrailingView trailingView;

        public TrailingViewListener(TrailingView view) {
            this.trailingView = view;
        }

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            PathView.this.removeView(trailingView);
            sPool.release(trailingView);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    static class BezierAnimUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        private final TrailingView imageView;
        private final float start;
        private final float end;

        public BezierAnimUpdateListener(TrailingView view, float start, float end) {
            imageView = view;
            this.start = start;
            this.end = end;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float value = (float) animation.getAnimatedValue();
            float a = (value - start) / (end - start);
            imageView.setAlpha(1 - a);
        }
    }

    private static class TrailingView extends View {
        public TrailingView(Context context) {
            super(context);
        }

        public TrailingView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public TrailingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        private final Paint mPaint = new Paint();
        private float mRadius = 8f;
        private float mAlpha = 1f;
        public void setColor(int color) {
            mPaint.setColor(color);
        }

        public void setRadius(float radius) {
            mRadius = radius;
        }

        public void setAlpha(float alpha){
            mAlpha = alpha;
            invalidate();
        }

        @SuppressLint("DrawAllocation")
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            mPaint.setAntiAlias(false);
            mPaint.setDither(false);
            mPaint.setAlpha((int) (mAlpha * 255));
            mPaint.setFilterBitmap(true);
            mPaint.setMaskFilter(new BlurMaskFilter(mRadius, BlurMaskFilter.Blur.NORMAL));
            canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        }
    }
}
