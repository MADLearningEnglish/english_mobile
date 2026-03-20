package com.mit.learning_english.domain.model

data class Sentence(
    val pagesId: Int,
    val id: Int,
    val content: String,
    val transcription1: String,
    val startTime: Int,
    val endTime: Int
)