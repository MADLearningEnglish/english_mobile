package com.mit.learning_english.domain.usecase.onboarding

import com.mit.learning_english.domain.repository.OnboardingRepository
import javax.inject.Inject

class CheckSeenOnboardingBeforeLoginUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) {
    suspend operator fun invoke () :Boolean{
        return onboardingRepository.checkSeenOnboardingBeforeLogin()
    }
}