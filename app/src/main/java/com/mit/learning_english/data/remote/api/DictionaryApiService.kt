package com.mit.learning_english.data.remote.api

import com.mit.learning_english.data.remote.dto.DictionaryEntryDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryApiService {
    @GET("https://api.dictionaryapi.dev/api/v2/entries/en/{word}")
    suspend fun getWordInfo(@Path("word") word: String): Response<List<DictionaryEntryDto>>
}
