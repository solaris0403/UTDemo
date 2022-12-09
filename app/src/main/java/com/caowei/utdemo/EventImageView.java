package com.caowei.utdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * 具有双击和长按事件的AppCompatImageView
 * 长按事件有按下和抬起的回调
 * 也可以添加单击事件，需要根据上次点击来判断是否触发单击、双击
 */
public class EventImageView extends AppCompatImageView implements View.OnLongClickListener, View.OnClickListener {
    private OnEventListener listener;
    private boolean mCurrentPressState = false;

    public EventImageView(@NonNull Context context) {
        super(context);
        initView();
    }

    public EventImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public EventImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setOnEventListener(OnEventListener listener) {
        this.listener = listener;
    }

    private void initView() {
        this.setOnLongClickListener(this);
        this.setOnClickListener(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mCurrentPressState && listener != null){
                listener.onLongPress(MotionEvent.ACTION_UP);
            }
            mCurrentPressState = false;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onLongClick(View v) {
        mCurrentPressState = true;
        if (this.listener != null) {
            this.listener.onLongPress(MotionEvent.ACTION_DOWN);
        }
        return false;
    }

    private long mLastClickTime;

    @Override
    public void onClick(View v) {
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < 250){
            if (listener != null) {
                listener.onDoubleClick();
            }
        }else{
            mLastClickTime = now;
        }
    }

    public interface OnEventListener {
        void onDoubleClick();

        /**
         *
         * @param action MotionEvent.ACTION_UP&MotionEvent.ACTION_DOWN
         */
        void onLongPress(int action);
    }
}
