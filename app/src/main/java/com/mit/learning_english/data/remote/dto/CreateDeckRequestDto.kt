package com.mit.learning_english.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateDeckRequestDto(
    @SerializedName("title") val title: String,
    @SerializedName("coverImageUrl") val coverImageUrl: String? = null,
    @SerializedName("flashcards") val flashcards: List<FlashcardCreateDto>
)

data class FlashcardCreateDto(
    @SerializedName("word") val word: String,
    @SerializedName("phonetic") val phonetic: String? = null,
    @SerializedName("meaning") val meaning: String,
    @SerializedName("exampleSentence") val exampleSentence: String? = null,
    @SerializedName("visualCueUrl") val visualCueUrl: String? = null,
    @SerializedName("note") val note: String? = null
)
