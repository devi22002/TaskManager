package com.example.taskmanager.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun formatMillisToDate(millis: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(millis))
    }
}
