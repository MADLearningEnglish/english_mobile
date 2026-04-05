package com.mit.learning_english.data.remote.api

import com.mit.learning_english.data.remote.dto.BaseResponse
import com.mit.learning_english.data.remote.dto.LevelDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LevelApiService {
    @GET("level/v1")
    suspend fun getLevels(): Response<BaseResponse<List<LevelDto>>>


}
