package com.mit.learning_english.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateDeckRequestDto(
    @SerializedName("title") val title: String,

    @SerializedName("status") val status: Int,
    @SerializedName("flashcards") val flashcards: List<FlashcardUpdateDto>
)

data class FlashcardUpdateDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("term") val term: String,
    @SerializedName("definition") val definition: String,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("status") val status: Int? = null
)
