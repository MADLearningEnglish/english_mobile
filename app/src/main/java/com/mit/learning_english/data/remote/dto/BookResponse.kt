package com.mit.learning_english.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.mit.learning_english.domain.model.Chapter

data class BookResponse(
    val id: Int,
    val title: String,
    val language: String,
    val coverUrl: String,
    val genresName: String,
    @SerializedName("authors") val authorsName: String,
    val chapters: List<Chapter>? = null,
    @SerializedName("lastReadNumberPage") val lastReadNumberPage: Int = 0,
    @SerializedName("progressPercent") val progressPercent: Double = 0.0,
    @SerializedName("lastReadTime") val lastReadTime: String? = null,
    @SerializedName("isFavorite") val isFavorite: Boolean = false
)
