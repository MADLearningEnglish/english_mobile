package com.mit.learning_english.data.repository

import com.mit.learning_english.data.local.datastore.PreferencesDatasource
import com.mit.learning_english.data.mapper.ResultMapper
import com.mit.learning_english.data.remote.api.OnboardingApiService
import com.mit.learning_english.domain.repository.OnboardingRepository
import javax.inject.Inject
import com.mit.learning_english.domain.util.Result

class OnboardingRepositoryImpl @Inject constructor(
    private val preferencesDatasource: PreferencesDatasource,
    private val onboardingApiService: OnboardingApiService,
    private val resultMapper: ResultMapper
): OnboardingRepository {
    override suspend fun checkSeenOnboardingBeforeLogin(): Boolean {
       return preferencesDatasource.hasSeenBeforeLoginOnboarding()
    }

    override suspend fun updateBeforeLoginOnboardingStatus(hasSeen: Boolean) {
        return preferencesDatasource.updateOnboardingStatus(hasSeen)
    }

    override suspend fun hasCompletedAfterLoginOnboarding(): Result<Boolean> {
        val response =  onboardingApiService.hasCompletedAfterLoginOnboarding()
        return resultMapper.fromBaseResponse(response)
    }

    override suspend fun setAfterLoginOnboardingCompleted(completed: Boolean):Result<Boolean> {
       val response =  onboardingApiService.setAfterLoginOnboardingCompleted(completed)
        return resultMapper.fromBaseResponse(response)
    }
}