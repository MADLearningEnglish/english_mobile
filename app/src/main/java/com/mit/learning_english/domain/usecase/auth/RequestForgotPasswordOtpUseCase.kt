package com.mit.learning_english.domain.usecase.auth

import com.mit.learning_english.domain.repository.AuthRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class RequestForgotPasswordOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Boolean> {
        // Repository should implement requestOtp internally and map Result<Boolean>
        return authRepository.requestForgotPasswordOtp(email)
    }
}