package com.mit.learning_english.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateDeckRequestDto(
    @SerializedName("title") val title: String,

    @SerializedName("flashcards") val flashcards: List<FlashcardCreateDto>
)

data class FlashcardCreateDto(
    @SerializedName("term") val term: String,
    @SerializedName("definition") val definition: String,
    @SerializedName("imageUrl") val imageUrl: String? = null
)
