package com.mit.learning_english.domain.model

data class UpdateFavoriteBookRequest(
    val bookId: Int, val isFavorite: Boolean
)