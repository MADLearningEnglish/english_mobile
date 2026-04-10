package com.mit.learning_english.data.remote.api

import com.mit.learning_english.data.remote.dto.ActivityDayDetailDto
import com.mit.learning_english.data.remote.dto.CorrectionSessionReviewDto
import com.mit.learning_english.data.remote.dto.LearningActivityItemDto
import com.mit.learning_english.data.remote.dto.UserCorrectionItemDto
import com.mit.learning_english.data.remote.dto.BaseResponse
import com.mit.learning_english.data.remote.dto.HeatmapDayDto
import com.mit.learning_english.data.remote.dto.LearningStatsOverviewDto
import com.mit.learning_english.data.remote.dto.ProfileMeDto
import com.mit.learning_english.data.remote.dto.ProfilePatchRequestDto
import com.mit.learning_english.data.remote.dto.SpringPageDto
import com.mit.learning_english.data.remote.dto.UserLearnedWordCreateDto
import com.mit.learning_english.data.remote.dto.UserLearnedWordDto
import com.mit.learning_english.data.remote.dto.UserLearnedWordPatchDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ProfileApiService {

    @GET("profile/v1/me")
    suspend fun getMe(): Response<BaseResponse<ProfileMeDto>>

    @PATCH("profile/v1/me")
    suspend fun patchMe(@Body body: ProfilePatchRequestDto): Response<BaseResponse<ProfileMeDto>>

    @Multipart
    @POST("profile/v1/me/avatar")
    suspend fun uploadAvatar(@Part file: MultipartBody.Part): Response<BaseResponse<ProfileMeDto>>

    @GET("profile/v1/stats/overview")
    suspend fun getStatsOverview(): Response<BaseResponse<LearningStatsOverviewDto>>

    @GET("profile/v1/activity/heatmap")
    suspend fun getHeatmap(
        @Query("from") from: String?,
        @Query("to") to: String?
    ): Response<BaseResponse<List<HeatmapDayDto>>>

    @GET("profile/v1/activity/day/{date}")
    suspend fun getActivityDay(@Path("date") date: String): Response<BaseResponse<ActivityDayDetailDto>>

    @GET("profile/v1/activity/history")
    suspend fun getActivityHistory(
        @Query("filter") filter: String,
        @Query("q") q: String?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<BaseResponse<SpringPageDto<LearningActivityItemDto>>>

    @GET("profile/v1/corrections")
    suspend fun getCorrections(
        @Query("filter") filter: String,
        @Query("q") q: String?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<BaseResponse<SpringPageDto<UserCorrectionItemDto>>>

    @GET("profile/v1/corrections/sessions/{sessionId}")
    suspend fun getCorrectionSession(
        @Path("sessionId") sessionId: Int
    ): Response<BaseResponse<CorrectionSessionReviewDto>>

    @DELETE("profile/v1/corrections")
    suspend fun clearCorrections(): Response<BaseResponse<Boolean>>

    @GET("profile/v1/vocabulary")
    suspend fun getVocabulary(
        @Query("filter") filter: String,
        @Query("q") q: String?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<BaseResponse<SpringPageDto<UserLearnedWordDto>>>

    @PATCH("profile/v1/vocabulary/{id}")
    suspend fun patchVocabulary(
        @Path("id") id: Int,
        @Body body: UserLearnedWordPatchDto
    ): Response<BaseResponse<UserLearnedWordDto>>

    @POST("profile/v1/vocabulary")
    suspend fun addVocabulary(
        @Body body: UserLearnedWordCreateDto
    ): Response<BaseResponse<UserLearnedWordDto>>
}
