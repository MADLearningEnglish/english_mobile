package com.mit.learning_english.domain.usecase.user

import com.mit.learning_english.domain.repository.UserProfileRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class UpdateUserLevelUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke(levelId: Int): Result<Boolean> {
        return userProfileRepository.updateLevel(levelId)
    }
}
