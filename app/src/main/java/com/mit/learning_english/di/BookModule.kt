package com.mit.learning_english.di

import com.mit.learning_english.data.mapper.ResultMapper
import com.mit.learning_english.data.remote.api.BookApiService
import com.mit.learning_english.data.repository.BookRepositoryImpl
import com.mit.learning_english.domain.repository.BookRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BookModule {

    @Provides
    @Singleton
    fun provideBookApiService(retrofit: Retrofit): BookApiService {
        return retrofit.create(BookApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideBookRepository(
        bookApiService: BookApiService,
        resultMapper: ResultMapper
    ): BookRepository {
        return BookRepositoryImpl(bookApiService, resultMapper)
    }
}
