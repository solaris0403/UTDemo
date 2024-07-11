package com.caowei.utils.extension

import android.graphics.LinearGradient
import android.graphics.Shader
import android.widget.TextView

fun TextView.setHShader(colors: IntArray){
    this.post {
        val shader = LinearGradient(0f, 0f, this.width.toFloat(), 0f, colors, null, Shader.TileMode.CLAMP)
        this.paint.setShader(shader)
        this.invalidate()
    }
}