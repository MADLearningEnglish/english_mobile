package com.mit.learning_english.di

import com.mit.learning_english.data.remote.api.AiChatApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiChatModule {

    @Provides
    @Singleton
    fun provideAiChatApiService(retrofit: Retrofit): AiChatApiService =
        retrofit.create(AiChatApiService::class.java)
}
