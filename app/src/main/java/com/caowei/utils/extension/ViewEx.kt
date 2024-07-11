package com.caowei.utils.extension

import android.graphics.Outline
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider

fun View.setRoundCorners(radius: Float) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, radius)
            }
        }
        clipToOutline = true
    } else {
        val radii = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        val shapeDrawable = ShapeDrawable(RoundRectShape(radii, null, null))
        background = shapeDrawable
    }
}