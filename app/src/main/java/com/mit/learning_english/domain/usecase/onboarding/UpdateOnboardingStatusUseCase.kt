package com.mit.learning_english.domain.usecase.onboarding

import com.mit.learning_english.domain.repository.OnboardingRepository
import javax.inject.Inject

class UpdateOnboardingStatusUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) {
    suspend operator fun invoke(hasSeen:Boolean){
        return onboardingRepository.updateBeforeLoginOnboardingStatus(hasSeen)
    }
}