package br.com.mobdhi.photosappvolvotest.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object Util {
    fun convertTimestampToLocalDate(timestamp: Long): LocalDate {
        val instant = Instant.ofEpochMilli(timestamp)
        val zoneId = ZoneId.systemDefault()
        return instant.atZone(zoneId).toLocalDate()
    }
}