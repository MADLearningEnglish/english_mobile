package com.mit.learning_english.data.remote.dto

data class PageResponse(
    val id: Int, val number: Int, val audio: AudioResponse, val sentence: List<SentenceResponse>
)



