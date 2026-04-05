package com.mit.learning_english.di

import com.mit.learning_english.data.remote.api.DeckApiService
import com.mit.learning_english.data.repository.DeckRepositoryImpl
import com.mit.learning_english.domain.repository.DeckRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DeckModule {

    @Provides
    @Singleton
    fun provideDeckApiService(retrofit: Retrofit): DeckApiService {
        // Lưu ý: Đảm bảo bạn truyền Retrofit instance có đính kèm Token/AuthInterceptor
        return retrofit.create(DeckApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDeckRepository(apiService: DeckApiService): DeckRepository {
        return DeckRepositoryImpl(apiService)
    }
}