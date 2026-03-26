package com.mit.learning_english.domain.repository

import com.mit.learning_english.domain.model.Genre
import com.mit.learning_english.domain.util.Result

interface GenreRepository {
    suspend fun getGenres(): Result<List<Genre>>
}