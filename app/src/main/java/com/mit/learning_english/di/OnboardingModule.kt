package com.mit.learning_english.di

import com.mit.learning_english.data.local.datastore.PreferencesDatasource
import com.mit.learning_english.data.mapper.ResultMapper
import com.mit.learning_english.data.remote.api.BookApiService
import com.mit.learning_english.data.remote.api.OnboardingApiService
import com.mit.learning_english.data.repository.OnboardingRepositoryImpl
import com.mit.learning_english.domain.repository.OnboardingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object OnboardingModule {

    @Provides
    @Singleton
    fun provideOnboardingApiService(
        retrofit: Retrofit
    ): OnboardingApiService{
         return retrofit.create(OnboardingApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideOnboardingRepository(
        onboardingApiService: OnboardingApiService,
        preferencesDatasource: PreferencesDatasource,
        resultMapper: ResultMapper
    ): OnboardingRepository{
        return OnboardingRepositoryImpl(preferencesDatasource = preferencesDatasource,onboardingApiService = onboardingApiService, resultMapper = resultMapper )
    }
}