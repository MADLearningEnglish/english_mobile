package com.mit.learning_english.domain.model

data class Flashcard(
    val id: Int,
    val term: String,
    val definition: String,
    val imageUrl: String?
)