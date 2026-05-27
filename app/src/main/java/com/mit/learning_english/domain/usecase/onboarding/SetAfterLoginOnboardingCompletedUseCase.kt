package com.mit.learning_english.domain.usecase.onboarding

import com.mit.learning_english.domain.repository.OnboardingRepository
import javax.inject.Inject

/**
 * Cập nhật trạng thái hoàn tất onboarding sau đăng nhập.
 */
class SetAfterLoginOnboardingCompletedUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) {
    /**
     * Đánh dấu đã/hoặc chưa hoàn tất onboarding sau đăng nhập.
     */
    suspend operator fun invoke(completed: Boolean = true) {
        onboardingRepository.setAfterLoginOnboardingCompleted(completed)
    }
}
