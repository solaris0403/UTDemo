package com.caowei.utdemo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.LogUtils
import com.caowei.video.AlphaVideoView
import com.caowei.video.AlphaVideoView.OnVideoStateListener

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var alphaVideoView: AlphaVideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        alphaVideoView = findViewById(R.id.alphaVideoView)
        findViewById<View>(R.id.btnStart).setOnClickListener {
            btnStart()
        }
        findViewById<View>(R.id.btnStop).setOnClickListener {
            btnStop()
        }
        alphaVideoView.setScaleType(AlphaVideoView.CENTER_INSIDE)
        alphaVideoView.setOnVideoStateListener(object : OnVideoStateListener{
            override fun onVideoStarted() {
                LogUtils.d(TAG, "onVideoStarted")
            }

            override fun onVideoEnded() {
                LogUtils.d(TAG, "onVideoEnded")
            }
        })
    }

    private fun btnStart() {
        alphaVideoView.setVideoFromAssets("leftColorRightAlpha_3.mp4")
    }

    private fun btnStop() {
        alphaVideoView.stop()
    }
}