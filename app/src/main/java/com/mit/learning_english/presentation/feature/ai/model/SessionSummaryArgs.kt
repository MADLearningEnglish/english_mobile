package com.mit.learning_english.presentation.feature.ai.model

import android.os.Parcelable
import com.mit.learning_english.data.remote.dto.EndSessionResponseDto
import kotlinx.parcelize.Parcelize

@Parcelize
data class SessionSummaryArgs(
    val durationMinutes: Int,
    val fluencyLevel: String?,
    val grammarLevel: String?,
    val vocabularyLevel: String?,
    val sentenceCount: Int,
    val errorCount: Int,
    val nextSuggestions: List<String>,
) : Parcelable

fun EndSessionResponseDto.toSessionSummaryArgs(): SessionSummaryArgs {
    val sec = durationSeconds ?: 0
    val mins = (sec / 60).coerceAtLeast(1)
    val list = nextSuggestions?.takeIf { it.isNotEmpty() }
        ?: nextSuggestion?.takeIf { it.isNotBlank() }?.let { listOf(it) }
        ?: emptyList()
    return SessionSummaryArgs(
        durationMinutes = mins,
        fluencyLevel = fluencyLevel,
        grammarLevel = grammarLevel,
        vocabularyLevel = vocabularyLevel,
        sentenceCount = sentenceCount ?: 0,
        errorCount = errorCount ?: 0,
        nextSuggestions = list,
    )
}
