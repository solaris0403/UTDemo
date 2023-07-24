package com.caowei.utdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ParticleView extends View {
    private Paint mPaint;
    private Bitmap dstBmp, srcBmp;
    private RectF dstRect, srcRect;

    public ParticleView(Context context) {
        super(context);
        initView();
    }

    public ParticleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ParticleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        dstBmp = BitmapFactory.decodeResource(getResources(), R.drawable.gnl);
        srcBmp = BitmapFactory.decodeResource(getResources(), R.drawable.noise_size_200);

        int index = 0;
        float bitmapWidth = dstBmp.getWidth();
        float bitmapHeight = dstBmp.getHeight();
        //将图片分割，然后保存21*21个坐标点
        for (int y = 0; y < HEIGHT; y++) {
            float fy = bitmapHeight / HEIGHT * y;
            for (int x = 0; x < WIDTH; x++) {
                float fx = bitmapWidth / WIDTH * x;
                //用数组保存坐标点fx, fy, 偶数位记录x坐标  奇数位记录Y坐标
                origs[index * 2 + 0] = verts[index * 2 + 0] = fx;
                origs[index * 2 + 1] = verts[index * 2 + 1] = fy;
                index++;
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int width = w <= h ? w : h;
        int centerX = w / 2;
        srcRect = new RectF(0, 0, width, width);

        float right = (h * 0.5f * dstBmp.getWidth() / dstBmp.getHeight());
        dstRect = new RectF(0, h * 0.25f, right, h * 0.75f);
    }

    //将图片划分成200个小格
    private static final int WIDTH = 200;
    private static final int HEIGHT = 200;
    //小格相交的总的点数
    private final int COUNT = (WIDTH + 1) * (HEIGHT + 1);
    private final float[] verts = new float[COUNT * 2];
    private final float[] origs = new float[COUNT * 2];
    private float k;


    private float move = 3f;
    private boolean rotateTop = true;
    private float maxRotate = 40;
    private float curRotate = 0;

    private float r = 0.3f;

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        //上下移动
        canvas.rotate(120, dstRect.right, (dstRect.top + dstRect.bottom) / 2);
//        if (rotateTop && Math.abs(curRotate) < maxRotate) {
//            canvas.rotate(r, dstRect.right, (dstRect.top + dstRect.bottom) / 2);
//            curRotate += r;
//        } else if (rotateTop && Math.abs(curRotate) >= maxRotate) {
//            rotateTop = false;
//            canvas.rotate(-r, dstRect.right, (dstRect.top + dstRect.bottom) / 2);
//            curRotate -= r;
//        } else if (!rotateTop && Math.abs(curRotate) < maxRotate) {
//            canvas.rotate(-r, dstRect.right, (dstRect.top + dstRect.bottom) / 2);
//            curRotate -= r;
//        }else if(!rotateTop && Math.abs(curRotate) >= maxRotate){
//            rotateTop = true;
//            canvas.rotate(r, dstRect.right, (dstRect.top + dstRect.bottom) / 2);
//            curRotate += r;
//        }


        mPaint.setMaskFilter(new BlurMaskFilter(50, BlurMaskFilter.Blur.NORMAL));
        //将绘制操作保存到新的图层，因为图像合成是很昂贵的操作，将用到硬件加速，这里将图像合成的处理放到离屏缓存中进行
        int saveCount = canvas.saveLayer(srcRect, mPaint, Canvas.ALL_SAVE_FLAG);
        //绘制目标图
        canvas.drawBitmap(dstBmp, null, dstRect, mPaint);
        //设置混合模式
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        //绘制源图
        canvas.drawBitmap(srcBmp, null, srcRect, mPaint);

        //清除混合模式
        mPaint.setXfermode(null);

        //循环背景
        if (srcRect.right <= dstRect.right) {
            srcRect.offsetTo(0, srcRect.top);
        }
        srcRect.offset(-move, 0);
        //还原画布
        canvas.restoreToCount(saveCount);


        invalidate();
    }
}
