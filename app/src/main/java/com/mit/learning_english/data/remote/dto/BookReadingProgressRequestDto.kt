package com.mit.learning_english.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class BookReadingProgressRequestDto(
    @SerializedName("lastReadPageNumber") val lastReadPageNumber: Int,
    @SerializedName("lastRead") val lastRead: LocalDateTime,
    @SerializedName("duration") val duration: Int
)