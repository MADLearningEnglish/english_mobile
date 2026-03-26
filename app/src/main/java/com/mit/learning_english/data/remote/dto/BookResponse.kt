package com.mit.learning_english.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.mit.learning_english.domain.model.Chapter
import java.time.LocalDateTime

data class BookResponse(
    val id: Int,
    val title: String,
    val language: String,
    val coverUrl: String,
    val genresName: String,
    @SerializedName("authors") val authorsName: String,
    val chapters: List<Chapter>?,
    @SerializedName("lastReadNumberPage") val lastReadNumberPage: Int,
    @SerializedName("progressPercent") val progressPercent: Double,
    @SerializedName("lastReadTime") val lastReadTime: LocalDateTime?,
    @SerializedName("isFavorite") val isFavorite: Boolean
)