package com.mit.learning_english.domain.usecase.onboarding

import com.mit.learning_english.domain.repository.OnboardingRepository
import javax.inject.Inject

class SetAfterLoginOnboardingCompletedUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) {
    suspend operator fun invoke(completed: Boolean = true) {
        onboardingRepository.setAfterLoginOnboardingCompleted(completed)
    }
}
