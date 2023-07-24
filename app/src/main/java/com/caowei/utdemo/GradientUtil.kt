package com.caowei.utdemo

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.blankj.utilcode.util.LogUtils

object GradientUtil {
    public fun setGradientLink(textView: TextView, colors: IntArray, vararg strings: String, clickable: (String) -> Unit){
        val spannableBuilder = SpannableStringBuilder(textView.text)
        strings.forEach {
            val start = spannableBuilder.indexOf(it)
            val end = start + it.length
            if(start < 0){
                return@forEach
            }
            val clickSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    clickable.invoke(it)
                }
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.linkColor = Color.TRANSPARENT
                    ds.isUnderlineText = false
                    ds.bgColor = Color.TRANSPARENT
                    ds.clearShadowLayer()
                }
            }
            val colorSpan = object : CharacterStyle() {
                override fun updateDrawState(paint: TextPaint) {
                    paint.shader = LinearGradient(
                        0f,
                        0f,
                        textView.measuredWidth.toFloat(),
                        0f,
                        colors,
                        null,
                        Shader.TileMode.CLAMP
                    )
                }
            }
            spannableBuilder.setSpan(clickSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableBuilder.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        textView.text = spannableBuilder
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = Color.TRANSPARENT
    }
}