package com.caowei.utdemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.caowei.utils.UTCache

class MainActivity : AppCompatActivity() {
    companion object{
        private const val TAG = "MainActivity"
    }
    private lateinit var mCache: UTCache
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        mCache = UTCache.get(this)
        mCache.put("caowei", "ddd")
        val value = mCache.getAsString("caowei")
        Log.e(TAG, value)
    }

    private fun initViewModel() {

    }

    private fun observeLivaData() {

    }

    private fun getData() {

    }
}