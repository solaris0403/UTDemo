package com.caowei.utdemo

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class SelectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)
        val btnOrder = findViewById<View>(R.id.btnOrder)
        btnOrder.setOnClickListener {
            val dialog = AlertDialog.Builder(this).setMessage("这是一个简单的对话框。")
                .setPositiveButton("返回", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        finish()
                    }
                })
            dialog.show()
        }
    }
}