package com.caowei.utdemo

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.LogUtils
import com.caowei.utils.LanguageUtils
import com.caowei.utils.extension.setHShader
import java.util.Locale


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tvTxt = findViewById<TextView>(R.id.tvTex)
        tvTxt.text = "asddasdasdasd"
        tvTxt.setHShader(intArrayOf(Color.RED, Color.BLUE, Color.GREEN))
    }
}