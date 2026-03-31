package com.mit.learning_english.di

import com.mit.learning_english.data.mapper.ResultMapper
import com.mit.learning_english.data.remote.api.GenreApiService
import com.mit.learning_english.data.repository.GenreRepositoryImpl
import com.mit.learning_english.domain.repository.GenreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GenreModule {

    @Provides
    @Singleton
    fun provideGenreApiService(retrofit: Retrofit): GenreApiService {
        return retrofit.create(GenreApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGenreRepository(
        genreApiService: GenreApiService,
        resultMapper: ResultMapper
    ): GenreRepository {
        return GenreRepositoryImpl(genreApiService, resultMapper)
    }
}
