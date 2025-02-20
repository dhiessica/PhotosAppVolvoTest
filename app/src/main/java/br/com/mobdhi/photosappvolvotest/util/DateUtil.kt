package br.com.mobdhi.photosappvolvotest.util

import java.text.DateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import java.util.Locale

object DateUtil {
    fun convertTimestampToLocalDate(timestamp: Long): String {
        val instant = Instant.ofEpochMilli(timestamp)
        val zoneId = ZoneId.systemDefault()
        val localDate = instant.atZone(zoneId).toLocalDate()
        return getFormattedDate(Date.from(localDate.atStartOfDay(zoneId).toInstant()))
    }

    fun getFormattedDate(date: Date): String {
        val locale = Locale.getDefault()
        val dateFormat: DateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale)
        return dateFormat.format(date)
    }
}
