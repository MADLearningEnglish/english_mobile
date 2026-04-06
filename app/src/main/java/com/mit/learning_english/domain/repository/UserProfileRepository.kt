package com.mit.learning_english.domain.repository

import com.mit.learning_english.domain.util.Result

interface UserProfileRepository {
    suspend fun updateLevel(levelId: Int): Result<Boolean>
    suspend fun updateFavoriteGenres(genreIds: List<Int>): Result<Boolean>
}
