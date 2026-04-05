package com.mit.learning_english.di

import com.mit.learning_english.data.mapper.ResultMapper
import com.mit.learning_english.data.remote.api.AuthApiService
import com.mit.learning_english.data.remote.retrofit.AuthManager
import com.mit.learning_english.data.repository.AuthRepositoryImpl
import com.mit.learning_english.di.qualifier.AuthRetrofit
import com.mit.learning_english.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthApiService(
        @AuthRetrofit retrofit: Retrofit
    ): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        authApiService: AuthApiService,
        authManager: AuthManager,
        resultMapper: ResultMapper
    ): AuthRepository {
        return AuthRepositoryImpl(
            authApiService = authApiService,
            authManager = authManager,
            resultMapper = resultMapper
        )
    }
}
