package com.mit.learning_english.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateDeckRequestDto(
    @SerializedName("title") val title: String,
    @SerializedName("coverImageUrl") val coverImageUrl: String? = null,
    @SerializedName("status") val status: Int,
    @SerializedName("flashcards") val flashcards: List<FlashcardUpdateDto>
)

data class FlashcardUpdateDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("word") val word: String,
    @SerializedName("phonetic") val phonetic: String? = null,
    @SerializedName("audioUrl") val audioUrl: String? = null,
    @SerializedName("partOfSpeech") val partOfSpeech: String? = null,
    @SerializedName("meaning") val meaning: String,
    @SerializedName("exampleSentence") val exampleSentence: String? = null,
    @SerializedName("visualCueUrl") val visualCueUrl: String? = null,
    @SerializedName("note") val note: String? = null,
    @SerializedName("status") val status: Int? = null
)
