package com.caowei.utdemo

import android.util.Log
import androidx.lifecycle.*

class MyObserver : LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> {
                Log.e("MainActivity", "ON_START: ")
            }
            Lifecycle.Event.ON_STOP -> {
                Log.e("MainActivity", "ON_STOP: ")
            }
            else -> {}
        }
    }
}