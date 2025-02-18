package br.com.mobdhi.photosappvolvotest.util

import java.text.DateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

object DateUtil {
    fun convertTimestampToLocalDate(timestamp: Long): LocalDate {
        val instant = Instant.ofEpochMilli(timestamp)
        val zoneId = ZoneId.systemDefault()
        return instant.atZone(zoneId).toLocalDate()
    }

    fun getFormattedCurrentDate(): String {
        val currentDate = Date()
        val locale = Locale.getDefault()
        val dateFormat: DateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale)
        return dateFormat.format(currentDate)
    }

    fun getTimestampFromDate(date: Date = Date()): Long {
        return date.time
    }
}
