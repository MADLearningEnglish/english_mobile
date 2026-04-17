package com.mit.learning_english.presentation.feature.profile.corrections

import android.content.Context
import com.mit.learning_english.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

object CorrectionTimeFormatter {

    fun bucketKey(epochMs: Long?, iso: String?): String {
        val d = parseLocalDate(epochMs, iso) ?: return ""
        return d.toString()
    }

    fun bucketTitle(epochMs: Long?, iso: String?): String {
        val d = parseLocalDate(epochMs, iso) ?: return ""
        val today = LocalDate.now()
        return when {
            d == today -> "Today"
            d == today.minusDays(1) -> "Yesterday"
            else -> d.format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.US))
        }
    }

    fun relativeShort(context: Context, epochMs: Long?, iso: String?): String {
        val ms = epochMs ?: iso?.let(::toEpochMillis) ?: return context.getString(R.string.profile_language_separator)
        val now = System.currentTimeMillis()
        val diffMs = now - ms
        if (diffMs < 0L) {
            // Client/server clock skew or backend epoch mismatch: treat as "just now".
            return context.getString(R.string.time_relative_just_now_vi)
        }
        val diffSeconds = diffMs / 1000L
        return when {
            diffSeconds < 60L -> context.getString(R.string.time_relative_seconds_vi, diffSeconds)
            diffSeconds < 3600L -> {
                val minutes = diffSeconds / 60L
                context.getString(R.string.time_relative_minutes_vi, minutes)
            }
            diffSeconds < 86_400L -> {
                val hours = diffSeconds / 3600L
                context.getString(R.string.time_relative_hours_vi, hours)
            }
            diffSeconds < 2_592_000L -> {
                val days = diffSeconds / 86_400L
                context.getString(R.string.time_relative_days_vi, days)
            }
            else -> {
                val d = parseLocalDate(ms, iso)
                d?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag("vi")))
                    ?: context.getString(R.string.profile_language_separator)
            }
        }
    }

    fun formatSessionDate(iso: String?): String {
        val d = parseLocalDate(null, iso) ?: return "—"
        return d.format(DateTimeFormatter.ofPattern("MMM d", Locale.US))
    }

    private fun parseLocalDate(epochMs: Long?, iso: String?): LocalDate? {
        if (epochMs != null) {
            return runCatching {
                java.time.Instant.ofEpochMilli(epochMs)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            }.getOrNull()
        }
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
        val raw = iso.trim()
        if (raw.isEmpty()) return null
        // If backend sends LocalDateTime without timezone, treat it as UTC to avoid "6h trước" drift.
        val hasZone = raw.endsWith("Z") || raw.matches(".*[+-]\\\\d{2}:?\\\\d{2}$".toRegex())
        return if (hasZone) {
            runCatching { OffsetDateTime.parse(raw).toInstant().toEpochMilli() }.getOrNull()
        } else {
            runCatching {
                val local = LocalDateTime.parse(raw.take(19))
                local.toInstant(ZoneOffset.UTC).toEpochMilli()
            }.getOrNull()
        }
    }
}
