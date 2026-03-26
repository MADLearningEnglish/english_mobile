package com.mit.learning_english.data.remote.api

import com.mit.learning_english.data.remote.dto.BaseResponse
import com.mit.learning_english.data.remote.dto.GenreResponse
import retrofit2.Response
import retrofit2.http.GET

interface GenreApiService {
    @GET("api/book/genres")
    suspend fun getGenres(): Response<BaseResponse<List<GenreResponse>>>
}