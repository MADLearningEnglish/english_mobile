package com.mit.learning_english.domain.usecase.onboarding

import com.mit.learning_english.domain.repository.OnboardingRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

/**
 * Kiểm tra người dùng đã hoàn tất onboarding sau đăng nhập hay chưa.
 */
class CheckSeenOnboardingAfterLoginUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) {
    /**
     * Trả về trạng thái đã xem onboarding sau đăng nhập.
     */
    suspend operator fun invoke(): Result<Boolean>{
        return onboardingRepository.hasCompletedAfterLoginOnboarding()
    }
}