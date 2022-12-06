package com.caowei.utdemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var mUserListViewModel: UserListViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mUserListViewModel = ViewModelProvider(this).get(UserListViewModel::class.java)
        mUserListViewModel.getLoadingLiveData().observe(this, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
                Log.i(TAG, "onChanged: $t")
            }
        })
        val btnStart: Button = findViewById(R.id.btn_start)
        btnStart.setOnClickListener {
            mUserListViewModel.setLoadingLiveData(true)
        }
    }

    private fun initView() {

    }

    private fun initViewModel() {

    }

    private fun observeLivaData() {

    }

    private fun getData() {

    }
}