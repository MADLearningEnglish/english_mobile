package com.mit.learning_english.domain.usecase

import com.mit.learning_english.domain.model.LoginRequest
import com.mit.learning_english.domain.repository.AuthRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Boolean> {
        val loginRequest = LoginRequest(email, password)
        return authRepository.login(loginRequest)
    }
}