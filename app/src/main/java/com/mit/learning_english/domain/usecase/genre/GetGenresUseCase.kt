package com.mit.learning_english.domain.usecase.genre

import com.mit.learning_english.domain.model.Genre
import com.mit.learning_english.domain.repository.GenreRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class GetGenresUseCase @Inject constructor(
    private val genreRepository: GenreRepository
) {
    suspend operator fun invoke(): Result<List<Genre>> {
        return genreRepository.getGenres()
    }
}