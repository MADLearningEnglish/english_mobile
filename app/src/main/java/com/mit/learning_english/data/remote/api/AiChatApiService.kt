package com.mit.learning_english.data.remote.api

import com.mit.learning_english.data.remote.dto.AiScenarioDto
import com.mit.learning_english.data.remote.dto.BaseResponse
import com.mit.learning_english.data.remote.dto.ChatMessageDetailItemDto
import com.mit.learning_english.data.remote.dto.ChatSessionHistoryItemDto
import com.mit.learning_english.data.remote.dto.CreateChatSessionRequestDto
import com.mit.learning_english.data.remote.dto.CreateChatSessionResponseDto
import com.mit.learning_english.data.remote.dto.EndSessionResponseDto
import com.mit.learning_english.data.remote.dto.SendTextMessageRequestDto
import com.mit.learning_english.data.remote.dto.SendTextMessageResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface AiChatApiService {

    @GET("ai-chat/v1/scenarios")
    suspend fun listScenarios(
        @Query("levelId") levelId: Int? = null,
        @Query("topicId") topicId: Int? = null,
        @Query("includeFreeChat") includeFreeChat: Boolean = true,
    ): Response<BaseResponse<List<AiScenarioDto>>>

    @POST("ai-chat/v1/sessions")
    suspend fun createSession(
        @Body body: CreateChatSessionRequestDto,
    ): Response<BaseResponse<CreateChatSessionResponseDto>>

    @POST("ai-chat/v1/sessions/{sessionId}/messages/text")
    suspend fun sendTextMessage(
        @Path("sessionId") sessionId: Int,
        @Body body: SendTextMessageRequestDto,
    ): Response<BaseResponse<SendTextMessageResponseDto>>

    @Multipart
    @POST("ai-chat/v1/sessions/{sessionId}/messages/voice")
    suspend fun sendVoiceMessage(
        @Path("sessionId") sessionId: Int,
        @Part audio: MultipartBody.Part,
        @Part("audioDuration") audioDuration: RequestBody?,
    ): Response<BaseResponse<SendTextMessageResponseDto>>

    @POST("ai-chat/v1/sessions/{sessionId}/end")
    suspend fun endSession(
        @Path("sessionId") sessionId: Int,
    ): Response<BaseResponse<EndSessionResponseDto>>

    @GET("ai-chat/v1/sessions")
    suspend fun sessionHistory(): Response<BaseResponse<List<ChatSessionHistoryItemDto>>>

    @GET("ai-chat/v1/sessions/{sessionId}/transcript")
    suspend fun sessionTranscript(
        @Path("sessionId") sessionId: Int,
    ): Response<BaseResponse<List<ChatMessageDetailItemDto>>>

    @DELETE("ai-chat/v1/sessions/{sessionId}")
    suspend fun deleteSession(
        @Path("sessionId") sessionId: Int,
    ): Response<BaseResponse<Any>>
}
