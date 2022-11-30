package com.caowei.utils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class OnDoubleClickListener implements View.OnTouchListener {
    private final GestureDetector mDetector;

    public OnDoubleClickListener(Context context) {
        this.mDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {//双击事件
                onDoubleClick();
                return super.onDoubleTap(e);
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    public abstract void onDoubleClick();
}
