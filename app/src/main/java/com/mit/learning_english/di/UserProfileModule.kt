package com.mit.learning_english.di

import com.mit.learning_english.data.mapper.ResultMapper
import com.mit.learning_english.data.remote.api.LevelApiService
import com.mit.learning_english.data.remote.api.UserProfileApiService
import com.mit.learning_english.data.repository.LevelRepositoryImpl
import com.mit.learning_english.data.repository.UserProfileRepositoryImpl
import com.mit.learning_english.domain.repository.LevelRepository
import com.mit.learning_english.domain.repository.UserProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserProfileModule {

    @Provides
    @Singleton
    fun provideUserProfileApiService(retrofit: Retrofit): UserProfileApiService {
        return retrofit.create(UserProfileApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideLevelApiService(retrofit: Retrofit): LevelApiService {
        return retrofit.create(LevelApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserProfileRepository(
        api: UserProfileApiService,
        resultMapper: ResultMapper
    ): UserProfileRepository {
        return UserProfileRepositoryImpl(api, resultMapper)
    }

    @Provides
    @Singleton
    fun provideLevelRepository(
        api: LevelApiService,
        resultMapper: ResultMapper
    ): LevelRepository {
        return LevelRepositoryImpl(api, resultMapper)
    }
}
