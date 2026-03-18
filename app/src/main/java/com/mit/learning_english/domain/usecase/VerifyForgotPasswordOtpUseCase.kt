package com.mit.learning_english.domain.usecase

import com.mit.learning_english.domain.repository.AuthRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class VerifyForgotPasswordOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, otp: String): Result<Boolean> {
        return authRepository.verifyForgotPasswordOtp(email, otp)
    }
}
