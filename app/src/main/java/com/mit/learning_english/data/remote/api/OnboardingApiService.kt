package com.mit.learning_english.data.remote.api

import com.mit.learning_english.data.remote.dto.BaseResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface OnboardingApiService {
    @PUT("onboarding/v1/update/{completed}")
    suspend fun setAfterLoginOnboardingCompleted(
        @Path("completed") completed: Boolean
    ): Response<BaseResponse<Boolean>>

    @GET("onboarding/v1/after-login")
    suspend fun hasCompletedAfterLoginOnboarding(): Response<BaseResponse<Boolean>>
}
