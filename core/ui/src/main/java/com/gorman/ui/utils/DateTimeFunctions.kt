package com.gorman.ui.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

fun getStartOfDay(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

fun getEndOfDay(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    return calendar.timeInMillis
}

fun getEndOfWeek(): Long {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, 7)
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    return calendar.timeInMillis
}

fun convertMillisToDate(millis: Long): String {
    val formatter = DateTimeFormatter.ofPattern("d MMMM")
    val instant = Instant.ofEpochMilli(millis)
    val date = instant.atZone(ZoneId.systemDefault()).toLocalDate()
    return date.format(formatter)
}

fun convertMillisToTime(millis: Long): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val instant = Instant.ofEpochMilli(millis)
    val time = instant.atZone(ZoneId.systemDefault()).toLocalTime()
    return time.format(formatter)
}
