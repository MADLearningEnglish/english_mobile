package com.mit.learning_english.di

import com.mit.learning_english.data.mapper.ResultMapper
import com.mit.learning_english.data.remote.api.ProfileApiService
import com.mit.learning_english.data.repository.ProfileRepositoryImpl
import com.mit.learning_english.domain.repository.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Provides
    @Singleton
    fun provideProfileApiService(retrofit: Retrofit): ProfileApiService =
        retrofit.create(ProfileApiService::class.java)

    @Provides
    @Singleton
    fun provideProfileRepository(
        api: ProfileApiService,
        resultMapper: ResultMapper
    ): ProfileRepository = ProfileRepositoryImpl(api, resultMapper)
}
