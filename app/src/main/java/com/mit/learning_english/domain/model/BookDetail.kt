package com.mit.learning_english.domain.model

data class BookDetail(
    val id: Int,
    val title: String,
    val language: String,
    val coverUrl: String,
    val genresName: String,
    val authorsName: String,
    val progressPercent: Double,
    val chapters: List<Chapter>,
    val lastReadNumberPage: Int,
    val isFavorite: Boolean,
)