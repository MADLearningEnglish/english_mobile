package com.mit.learning_english.data.remote.dto

data class SentenceResponse(
    val pagesId: Int,
    val id: Int,
    val content: String,
    val transcription1: String,
    val startTime: Int,
    val endTime: Int
)
