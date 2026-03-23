package com.mit.learning_english.data.remote.dto

data class PageResponse(
    val id: Int,
    val number: Int,
    val audio: AudioResponse,
    val sentence: List<SentenceResponse>
)

data class AudioResponse(
    val id: Int,
    val duration: Int,
    val format: String,
    val sampleRate: Int,
    val fileSize: Int,
    val fileUrl: String,
    val pagesId: Int
)

data class SentenceResponse(
    val pagesId: Int,
    val id: Int,
    val content: String,
    val transcription1: String,
    val startTime: Int,
    val endTime: Int
)
