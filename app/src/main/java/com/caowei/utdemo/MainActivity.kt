package com.caowei.utdemo

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val mLiveData: MutableLiveData<String> = MutableLiveData()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        lifecycle.addObserver(MyObserver())

        val btnStart: Button = findViewById(R.id.btn_start)
        btnStart.setOnClickListener {
//            startActivity(Intent(this, SecondActivity::class.java))
            mLiveData.value = System.currentTimeMillis().toString()
        }

        mLiveData.observe(this) {
            Log.e(TAG, it)
        }
        mLiveData.value = "onCreate"
    }

    override fun onStart() {
        super.onStart()
//        mLiveData.value = "onStart"
    }

    override fun onStop() {
        super.onStop()
        mLiveData.value = "onStop"
    }
}