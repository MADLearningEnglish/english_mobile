package com.mit.learning_english.data.remote.dto

data class AudioResponse(
    val id: Int,
    val duration: Int,
    val format: String,
    val sampleRate: Int,
    val fileSize: Int,
    val fileUrl: String,
    val pagesId: Int
)