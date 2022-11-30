package com.caowei.utils;

import java.util.Timer;
import java.util.TimerTask;

public class TimerScheduler {
    private static TimerScheduler sInstance;

    private TimerScheduler() {
    }

    public static TimerScheduler getInstance() {
        if (sInstance == null) {
            synchronized (TimerScheduler.class) {
                if (sInstance == null) {
                    sInstance = new TimerScheduler();
                }
            }
        }
        return sInstance;
    }

    private Timer mTimer;
    private TimerTask mTimerTask;

    public void schedule(TimerTask task, long delay, long period){
        cancel();
        mTimerTask = task;
        mTimer = new Timer();
        mTimer.schedule(mTimerTask, delay, period);
    }

    public void cancel(){
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }
}