package com.caowei.utils

import android.os.Build
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

/**
 * 日期时间处理
 * 使用JAVA8引入的DateTimeFormatter，线程安全
 */
object DateUtil {
    fun formatTimestamp(timestamp: Long, pattern: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val instant = Instant.ofEpochMilli(timestamp)
            val formatter = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault())
            formatter.format(instant)
        } else {
            val date = Date(timestamp)
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
            sdf.format(date)
        }
    }
}