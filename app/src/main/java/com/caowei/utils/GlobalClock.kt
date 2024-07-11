package com.caowei.utils

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*

/**
 * `GlobalClock` 是一个单例对象，用于在每秒钟向所有注册的观察者广播当前时间。
 * 它使用 Kotlin 协程在主线程中调度定时任务，并在没有观察者时自动停止。
 *
 * 功能包括：
 * - 注册和注销时钟观察者
 * - 自动管理时钟的启动和停止
 * - 与 `LifecycleOwner` 一起使用，自动管理生命周期感知的观察者
 */
object GlobalClock {
    private val observers = mutableSetOf<ClockObserver>()
    private var isRunning = false
    private var job: Job? = null

    /**
     * 注册一个时钟观察者，并在没有运行时启动时钟。
     *
     * @param observer 要注册的时钟观察者
     */
    fun registerObserver(observer: ClockObserver) {
        observers.add(observer)
        if (!isRunning) {
            startClock()
        }
    }

    /**
     * 注销一个时钟观察者，并在没有观察者时停止时钟。
     *
     * @param observer 要注销的时钟观察者
     */
    fun unregisterObserver(observer: ClockObserver) {
        observers.remove(observer)
        if (observers.isEmpty()) {
            stopClock()
        }
    }

    /**
     * 启动时钟，使用协程在主线程中每秒向所有观察者广播当前时间。
     */
    private fun startClock() {
        isRunning = true
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                val currentTime = System.currentTimeMillis()
                observers.forEach { it.onTick(currentTime) }
                delay(1000)
            }
        }
    }

    /**
     * 停止时钟，取消正在运行的协程。
     */
    private fun stopClock() {
        isRunning = false
        job?.cancel()
        job = null
    }

    /**
     * 使用 `LifecycleOwner` 注册一个时钟观察者。
     * 观察者在生命周期 `onResume` 时注册，在 `onPause` 时注销。
     *
     * @param lifecycleOwner 用于管理观察者生命周期的 `LifecycleOwner`
     * @param onTick 当时钟滴答时要执行的回调
     */
    fun registerObserver(lifecycleOwner: LifecycleOwner, onTick: (Long) -> Unit) {
        val observer = object : ClockObserver, DefaultLifecycleObserver {
            override fun onTick(time: Long) {
                onTick(time)
            }

            override fun onResume(owner: LifecycleOwner) {
                registerObserver(this)
            }

            override fun onPause(owner: LifecycleOwner) {
                unregisterObserver(this)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
    }
}

/**
 * `ClockObserver` 接口定义了一个方法，用于接收时间更新。
 */
interface ClockObserver {
    /**
     * 当时钟滴答时调用此方法。
     *
     * @param time 当前时间的毫秒数
     */
    fun onTick(time: Long)
}
