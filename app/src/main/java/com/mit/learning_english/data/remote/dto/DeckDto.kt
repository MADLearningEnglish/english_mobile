package com.mit.learning_english.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DeckDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,

    @SerializedName("status") val status: Int,
    @SerializedName("flashcards") val flashcards: List<FlashcardDto>?
)

data class FlashcardDto(
    @SerializedName("id") val id: Int,
    @SerializedName("term") val term: String?,
    @SerializedName("definition") val definition: String?,
    @SerializedName("imageUrl") val imageUrl: String?
)

data class StudyResultDto(
    @SerializedName("totalWords") val totalWords: Int,
    @SerializedName("masteryPercentage") val masteryPercentage: Double,
    @SerializedName("knownCount") val knownCount: Long,
    @SerializedName("easyCount") val easyCount: Long,
    @SerializedName("mediumCount") val mediumCount: Long,
    @SerializedName("hardCount") val hardCount: Long
)