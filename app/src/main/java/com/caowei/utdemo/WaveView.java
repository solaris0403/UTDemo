package com.caowei.utdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

public class WaveView extends View {

    private static int DRAW_BOAT_OFFSET = 15;
//    private Bitmap bitmap;
    private int mWaveLength;
    private Path mPath;
    private int mHeight;
    private int mWidth;
    private Paint mPaint;
    private Matrix mMatrix;
    private PathMeasure pathMeasure;
    private float animatedValue;

    public WaveView(Context context) {
        this(context,null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 加载小船图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
//        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher, options);

        // 初始化需要用到的变量
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.argb(105,0,0,255));
        mPaint.setStyle(Paint.Style.FILL);
        mPath = new Path();
        mMatrix = new Matrix();
        pathMeasure = new PathMeasure();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = getWidth();
        mHeight = getHeight();
        mWaveLength = mWidth / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPath.reset();
        // 根据动画的进行进度设置波浪形的path路径，并绘制波浪
        mPath.moveTo(-mWaveLength+(mWaveLength/2)*animatedValue,mHeight/2);
        for (int i = -mWaveLength; i < mWidth + mWaveLength; i+=mWaveLength) {
            mPath.rQuadTo(mWaveLength / 2 +(mWaveLength/2)*animatedValue,
                    60,
                    mWaveLength+(mWaveLength/2)*animatedValue,
                    0);
            mPath.rQuadTo(mWaveLength / 2 +(mWaveLength/2)*animatedValue,
                    -60,
                    mWaveLength+(mWaveLength/2)*animatedValue,
                    0);
        }
        mPath.lineTo(mWidth,mHeight);
        mPath.lineTo(0,mHeight);
        mPath.close();
        canvas.drawPath(mPath,mPaint);

        // 因为每次绘制的path可能都不同，所以每次都为pathMeasure设置path
        pathMeasure.setPath(mPath,false);
        // 根据动画进行的进度取出当前长度的matrix
        boolean matrix = pathMeasure.getMatrix(animatedValue * pathMeasure.getLength(), mMatrix,
                PathMeasure.TANGENT_MATRIX_FLAG | PathMeasure.POSITION_MATRIX_FLAG);
        if (matrix){
            // 操作matrix，绘制小船
//            mMatrix.preTranslate(-bitmap.getWidth() / 2,-bitmap.getHeight() + DRAW_BOAT_OFFSET);
//            canvas.drawBitmap(bitmap,mMatrix,null);
        }
    }

    // 开启动画
    public void startAnimator(){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0,1);
        valueAnimator.setDuration(15000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatedValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
    }
}
