package com.mit.learning_english.di

import com.mit.learning_english.data.mapper.ResultMapper
import com.mit.learning_english.data.remote.api.FileApiService
import com.mit.learning_english.data.repository.FileRepositoryImpl
import com.mit.learning_english.domain.repository.FileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FileModule {

    @Provides
    @Singleton
    fun provideFileApiService(retrofit: Retrofit): FileApiService {
        return retrofit.create(FileApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFileRepository(
        apiService: FileApiService,
        resultMapper: ResultMapper
    ): FileRepository {
        return FileRepositoryImpl(apiService, resultMapper)
    }
}
