package com.mit.learning_english.domain.usecase.onboarding

import com.mit.learning_english.domain.repository.OnboardingRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class CheckSeenOnboardingAfterLoginUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) {
    suspend operator fun invoke(): Result<Boolean>{
        return onboardingRepository.hasCompletedAfterLoginOnboarding()
    }
}