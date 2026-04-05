package com.mit.learning_english.domain.usecase.user

import com.mit.learning_english.domain.repository.UserProfileRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class UpdateUserFavoriteGenresUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke(genreIds: List<Int>): Result<Boolean> {
        return userProfileRepository.updateFavoriteGenres(genreIds)
    }
}
