package com.mit.learning_english.domain.usecase

import com.mit.learning_english.domain.repository.AuthRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
	private val authRepository: AuthRepository
) {
	suspend operator fun invoke(email: String, password: String, fullName: String): Result<Boolean> {
		return authRepository.signUp(email, password, fullName)
	}
}