package com.mit.learning_english.di

import com.mit.learning_english.data.remote.api.PageApiService
import com.mit.learning_english.data.repository.PageRepositoryImpl
import com.mit.learning_english.domain.repository.PageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PageModule {

    @Provides
    @Singleton
    fun providePageApiService(retrofit: Retrofit): PageApiService {
        return retrofit.create(PageApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePageRepository(
        pageApiService: PageApiService
    ): PageRepository {
        return PageRepositoryImpl(pageApiService)
    }
}
