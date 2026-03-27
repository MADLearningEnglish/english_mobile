package com.mit.learning_english.data.repository

import android.util.Log
import com.mit.learning_english.data.mapper.ResultMapper
import com.mit.learning_english.data.mapper.toGenre
import com.mit.learning_english.data.remote.api.GenreApiService
import com.mit.learning_english.domain.model.Genre
import com.mit.learning_english.domain.repository.GenreRepository
import com.mit.learning_english.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GenreRepositoryImpl @Inject constructor(
    private val genreApiService: GenreApiService, private val resultMapper: ResultMapper
) : GenreRepository {
    override suspend fun getGenres(): Result<List<Genre>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = genreApiService.getGenres()
                Log.d("GenreRepositoryImpl", response.body().toString())
                resultMapper.fromBaseResponse(response).map { list -> list.map { it.toGenre() } }
            } catch (e: Exception) {
                resultMapper.fromException(e)
            }
        }
    }
}