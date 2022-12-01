package com.caowei.utdemo

import android.app.Application
import android.util.Log
import androidx.lifecycle.*

class AppContext : Application() {
    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(ApplicationLifecycleObserver())
    }

    inner class ApplicationLifecycleObserver : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            Log.e("AppContext", source.toString() + event.name)
        }
    }
}