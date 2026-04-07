package com.mit.learning_english.domain.model

data class Book(
    val id: Int,
    val title: String,
    val language: String,
    val coverUrl: String,
    val blurb: String,
    val genresName: String,
    val authorsName: String,
    val chapters: List<Chapter>,
)
