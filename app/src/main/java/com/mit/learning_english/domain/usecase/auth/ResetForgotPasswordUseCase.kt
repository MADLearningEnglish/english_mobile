package com.mit.learning_english.domain.usecase.auth

import com.mit.learning_english.domain.repository.AuthRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class ResetForgotPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, otp: String, newPassword: String): Result<Boolean> {
        return authRepository.resetForgotPassword(email, otp, newPassword)
    }
}