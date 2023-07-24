package com.caowei.utdemo;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import java.util.ArrayList;

public class PraiseView extends FrameLayout implements View.OnClickListener, LifecycleObserver {

    private static PointF start;
    private float v;
    private int measuredWidth;
    private ArrayList<ImageView> imageViews;
    private ImageView view;
    private int count = 0;

    private ArrayList<Integer> resIds;
    private int minMultiplicator = 0;
    private int maxMultiplicator = 5;
    private int maxNumExpression = 5;
    private int initExpressionSize = dp2px(10);
    private int duration = 1500;
    private LayoutParams layoutParams;

    private AnimatorSetConfig config;

    public PraiseView(Context context) {
        this(context, null);
    }

    public PraiseView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PraiseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (context instanceof LifecycleOwner) {
            ((LifecycleOwner) context).getLifecycle().addObserver(this);
        }
        Drawable drawable = null;
        int buttonW = dp2px(20);
        int buttonH = dp2px(20);
        boolean isShowButton = true;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PraiseView);
        if (typedArray != null) {
            isShowButton = typedArray.getBoolean(R.styleable.PraiseView_isShowButton, true);
            buttonW = typedArray.getDimensionPixelSize(R.styleable.PraiseView_button_width, buttonW);
            buttonH = typedArray.getDimensionPixelSize(R.styleable.PraiseView_button_height, buttonH);
            initExpressionSize = typedArray.getDimensionPixelSize(R.styleable.PraiseView_expression_size, initExpressionSize);
            drawable = typedArray.getDrawable(R.styleable.PraiseView_button_icon_res);
            duration = typedArray.getInteger(R.styleable.PraiseView_anim_duration, duration);
            maxMultiplicator = typedArray.getInteger(R.styleable.PraiseView_multiplying_power, maxMultiplicator);//倍率(放大倍率)
            maxNumExpression = typedArray.getInteger(R.styleable.PraiseView_expression_max_num, maxNumExpression);//倍率(同一张资源图同时出现的最大数)
            //获取arrays.xml文件中图片资源集合
            int resourceId = typedArray.getResourceId(R.styleable.PraiseView_expression_list, -1);
            if (resourceId != -1) {
                resIds = new ArrayList<>();
                TypedArray array = getResources().obtainTypedArray(resourceId);
                for (int i = 0; i < array.length(); i++) {
                    int resourceId1 = array.getResourceId(i, -1);
                    if (resourceId1 != -1) {
                        resIds.add(resourceId1);
                    }
                    array.recycle();
                }
            }
            typedArray.recycle();
        }
        view = new ImageView(context);
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        setButtonSize(buttonW, buttonH);
        view.setOnClickListener(this);
        if (drawable != null) {
            view.setImageDrawable(drawable);
        } else {
            view.setBackgroundColor(Color.RED);
        }
        imageViews = new ArrayList<>();
        addView(view);
        isShowButton(isShowButton);
    }

    /**
     * 设置按钮图片资源
     */
    public void setButtonBgRes(@DrawableRes int res) {
        if (view != null) {
            view.setImageResource(res);
        }
    }

    /**
     * 是否显示按钮
     */
    public void isShowButton(boolean isShow) {
        if (view != null) {
            view.setEnabled(isShow);
            view.setVisibility(isShow ? VISIBLE : GONE);
        }
    }

    /**
     * 设置按钮尺寸
     */
    public void setButtonSize(int wdp, int hdp) {
        if (layoutParams != null && view != null) {
            layoutParams.height = dp2px(wdp);
            layoutParams.width = dp2px(hdp);
            view.setLayoutParams(layoutParams);
        }
    }

    /**
     * 设置动画图片资源
     */
    public void setImageResIds(ArrayList<Integer> resIds) {
        this.resIds = resIds;
    }

    /**
     * 设置动画图片放大倍率
     */
    public void setMaxMultiplicator(@IntRange(from = 1) int maxMultiplicator) {
        this.maxMultiplicator = maxMultiplicator;
    }

    /**
     * 设置动画图片最小显示倍率
     */
    public void setMinMultiplicator(@IntRange(from = 0) int minMultiplicator) {
        this.minMultiplicator = minMultiplicator;
    }

    /**
     * 设置同一时间同一资源图片在页面的最大显示数量
     */
    public void setMaxNumExpression(@IntRange(from = 1) int maxNumExpression) {
        this.maxNumExpression = maxNumExpression;
    }

    /**
     * 设置资源图片尺寸(最大显示尺寸= 资源图片尺寸*(放大倍率+最小显示倍率)
     */
    public void setInitExpressionSize(int dp) {
        initExpressionSize = dp2px(dp);
    }

    /**
     * 自定义动画集合
     */
    public void setAnimators(AnimatorSetConfig config) {
        this.config = config;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        v = measuredHeight / 5f;
        start = new PointF(measuredWidth / 2f, measuredHeight - (view.getMeasuredHeight() / 2f));
    }

    /**
     * 播放动画
     */
    public void playAnim() {
        if (resIds == null || resIds.size() <= 0) {
            return;
        }
        AnimatorSet animatorSet = null;
        if (imageViews.size() > count && imageViews.get(count) != null) {
            animatorSet = (AnimatorSet) imageViews.get(count).getTag(R.id.image_anim);
        } else {
            ImageView imageView = new ImageView(getContext());
            LayoutParams layoutParams = new LayoutParams(initExpressionSize, initExpressionSize);
            layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            imageView.setLayoutParams(layoutParams);
            imageView.setImageResource(resIds.get(count % resIds.size()));
            imageViews.add(imageView);
            addView(imageView);

            animatorSet = new AnimatorSet();
            BaseBezierAnimUpdateListener myUpdateListener = null;
            if (config == null) {
                //贝塞尔曲线运动动画(含放大动画)
                ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
                myUpdateListener = new BezierAnimUpdateListener(imageView);
                animator.addUpdateListener(myUpdateListener);
                animator.setInterpolator(new LinearInterpolator());
                //渐变动画
                ValueAnimator alpha = ValueAnimator.ofFloat(1, 0);
                alpha.addUpdateListener(new AlphaAnimUpdateListener(imageView));
                alpha.setInterpolator(new AccelerateInterpolator());
                animatorSet.playTogether(animator, alpha);
            } else {
                myUpdateListener = config.config(animatorSet, imageView);
            }
            animatorSet.setDuration(duration);
            imageView.setTag(R.id.image_anim, animatorSet);
            imageView.setTag(R.id.image_anim_update, myUpdateListener);
        }

        if (animatorSet != null && !animatorSet.isRunning()) {
            if (imageViews.get(count).getTag(R.id.image_anim_update) != null) {
                ((BaseBezierAnimUpdateListener) imageViews.get(count).getTag(R.id.image_anim_update)).resettingPoint();
            }
            animatorSet.start();
        }
        count++;
        if (count >= resIds.size() * maxNumExpression) {
            count = 0;
        }
    }

    /**
     * 获取X轴随机点
     *
     * @param y 轴位置
     */
    public PointF getRandomP(float y) {
        return new PointF((float) Math.random() * measuredWidth, y);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        isPauseAnim(false);
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        isPauseAnim(true);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        for (ImageView image : imageViews) {
            if (image != null) {
                if (image.getTag(R.id.image_anim) != null) {
                    ((AnimatorSet) image.getTag(R.id.image_anim)).cancel();
                }
            }
            image = null;
        }
        imageViews.clear();
        imageViews = null;
        if (resIds != null) {
            resIds.clear();
        }
        if (getContext() instanceof LifecycleOwner) {
            ((LifecycleOwner) getContext()).getLifecycle().removeObserver(this);
        }
        removeAllViews();
    }

    public int dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void isPauseAnim(boolean isPause) {
        for (ImageView image : imageViews) {
            if (image != null && image.getTag(R.id.image_anim) != null) {
                AnimatorSet animator = (AnimatorSet) image.getTag(R.id.image_anim);
                if (isPause) {
                    if (animator.isRunning()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            animator.pause();
                        } else {
                            animator.end();
                        }
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && animator.isPaused()) {
                        animator.resume();
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        playAnim();
    }

    public interface AnimatorSetConfig {
        BaseBezierAnimUpdateListener config(AnimatorSet set, ImageView imageView);
    }

    private class AlphaAnimUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        private ImageView imageView;

        public AlphaAnimUpdateListener(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            imageView.setAlpha((Float) animation.getAnimatedValue());
        }
    }

    public abstract static class BaseBezierAnimUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        private ImageView imageView;

        public BaseBezierAnimUpdateListener(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            onAnimationUpdate(animation, start, imageView);
        }

        public abstract void onAnimationUpdate(ValueAnimator animation, PointF startP, ImageView imageView);

        public abstract void resettingPoint();
    }

    private class BezierAnimUpdateListener extends BaseBezierAnimUpdateListener {
        private PointF p1;
        private PointF p2;
        private PointF end;

        public BezierAnimUpdateListener(ImageView imageView) {
            super(imageView);
            resettingPoint();
        }

        @Override
        public void resettingPoint() {
            p1 = getRandomP((float) Math.random() * v + (3 * v));
            p2 = getRandomP((float) Math.round(2) * v + (2 * v));
            end = getRandomP((float) Math.random() * v);
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation, PointF startP, ImageView imageView) {
            float t = (float) animation.getAnimatedValue();
            PointF bezierPoint = BezierUtils.getBezierPoint(t, startP, p1, p2, end);
            imageView.setY(bezierPoint.y);
            imageView.setX(bezierPoint.x);
            imageView.setScaleY(t * maxMultiplicator + minMultiplicator);
            imageView.setScaleX(t * maxMultiplicator + minMultiplicator);
        }
    }
}

