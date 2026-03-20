package com.mit.learning_english.domain.model

data class Audio(
    val id: Int,
    val duration: Int,
    val format: String,
    val sampleRate: Int,
    val fileSize: Int,
    val fileUrl: String,
    val pagesId: Int
)
