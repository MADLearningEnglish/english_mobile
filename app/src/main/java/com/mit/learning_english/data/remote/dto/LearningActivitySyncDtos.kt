package com.mit.learning_english.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DeckStudyCompleteRequestDto(
    @SerializedName("durationSeconds") val durationSeconds: Int,
    @SerializedName("cardsReviewed") val cardsReviewed: Int? = null,
    @SerializedName("quizCorrect") val quizCorrect: Int? = null,
    @SerializedName("quizTotal") val quizTotal: Int? = null
)

data class BookReadingProgressRequestDto(
    @SerializedName("lastReadPageNumber") val lastReadPageNumber: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("durationSeconds") val durationSeconds: Int? = null
)
