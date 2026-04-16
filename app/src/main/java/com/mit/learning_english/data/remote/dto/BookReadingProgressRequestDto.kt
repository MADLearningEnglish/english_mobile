package com.mit.learning_english.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BookReadingProgressRequestDto(
    @SerializedName("lastReadPageNumber") val lastReadPageNumber: Int,
    @SerializedName("lastRead") val lastRead: String,
    @SerializedName("duration") val duration: Int
)