package com.mit.learning_english.presentation.feature.profile.corrections

import android.content.Context
import android.text.format.DateUtils
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object CorrectionTimeFormatter {

    fun bucketKey(iso: String?): String {
        val d = parseLocalDate(iso) ?: return ""
        return d.toString()
    }

    fun bucketTitle(iso: String?): String {
        val d = parseLocalDate(iso) ?: return ""
        val today = LocalDate.now()
        return when {
            d == today -> "Today"
            d == today.minusDays(1) -> "Yesterday"
            else -> d.format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.US))
        }
    }

    fun relativeShort(context: Context, iso: String?): String {
        if (iso.isNullOrBlank()) return "—"
        val ms = toEpochMillis(iso) ?: return iso
        return DateUtils.getRelativeTimeSpanString(
            ms,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS
        ).toString()
    }

    fun formatSessionDate(iso: String?): String {
        val d = parseLocalDate(iso) ?: return "—"
        return d.format(DateTimeFormatter.ofPattern("MMM d", Locale.US))
    }

    private fun parseLocalDate(iso: String?): LocalDate? {
        if (iso.isNullOrBlank()) return null
        return try {
            LocalDate.parse(iso.take(10))
        } catch (_: Exception) {
            try {
                LocalDateTime.parse(iso.take(19)).toLocalDate()
            } catch (_: Exception) {
                null
            }
        }
    }

    private fun toEpochMillis(iso: String): Long? {
        return try {
            val ldt = LocalDateTime.parse(iso.take(19))
            ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        } catch (_: Exception) {
            null
        }
    }
}
