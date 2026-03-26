package com.mit.learning_english.domain.model

data class Flashcard(
    val id: Int,
    val word: String,
    val phonetic: String?,
    val meaning: String,
    val partOfSpeech: String?,
    val exampleSentence: String?,
    val note: String?,
    val visualCueUrl: String?
)