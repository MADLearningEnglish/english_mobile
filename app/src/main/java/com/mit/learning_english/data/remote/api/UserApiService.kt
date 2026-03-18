package com.mit.learning_english.data.remote.api

import com.mit.learning_english.data.remote.dto.BaseResponse
import com.mit.learning_english.data.remote.dto.CreateUserRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


/**
 * API for user-related public endpoints (register/create user)
 */
interface UserApiService {
    @POST("user/v1")
    suspend fun createUser(@Body request: CreateUserRequest): Response<BaseResponse<Any?>>
}
