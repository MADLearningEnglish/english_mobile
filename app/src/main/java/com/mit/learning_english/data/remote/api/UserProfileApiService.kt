package com.mit.learning_english.data.remote.api

import com.mit.learning_english.data.remote.dto.BaseResponse
import com.mit.learning_english.data.remote.dto.UpdateUserFavoriteGenresRequest
import com.mit.learning_english.data.remote.dto.UpdateUserLevelRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT

interface UserProfileApiService {

    @PUT("level/v1")
    suspend fun updateLevel(@Body body: UpdateUserLevelRequest): Response<BaseResponse<Boolean>>

    @PUT("genre/v1/favorite-genres")
    suspend fun updateFavoriteGenres(@Body body: UpdateUserFavoriteGenresRequest): Response<BaseResponse<Boolean>>
}
