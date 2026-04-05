package com.mit.learning_english.data.repository

import com.mit.learning_english.data.remote.api.AiChatApiService
import com.mit.learning_english.data.remote.dto.AiScenarioDto
import com.mit.learning_english.data.remote.dto.ChatMessageDetailItemDto
import com.mit.learning_english.data.remote.dto.ChatSessionHistoryItemDto
import com.mit.learning_english.data.remote.dto.CreateChatSessionRequestDto
import com.mit.learning_english.data.remote.dto.CreateChatSessionResponseDto
import com.mit.learning_english.data.remote.dto.EndSessionResponseDto
import com.mit.learning_english.data.remote.dto.SendTextMessageRequestDto
import com.mit.learning_english.data.remote.dto.SendTextMessageResponseDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiChatRepository @Inject constructor(
    private val api: AiChatApiService,
) {

    suspend fun listScenarios(
        levelId: Int? = null,
        topicId: Int? = null,
        includeFreeChat: Boolean = true,
    ): Result<List<AiScenarioDto>> =
        runCatching {
            val res = api.listScenarios(levelId, topicId, includeFreeChat)
            if (!res.isSuccessful) error(res.errorBody()?.string() ?: res.message())
            res.body()?.data ?: emptyList()
        }

    suspend fun createSession(request: CreateChatSessionRequestDto): Result<CreateChatSessionResponseDto> =
        runCatching {
            val res = api.createSession(request)
            if (!res.isSuccessful) error(res.errorBody()?.string() ?: res.message())
            res.body()?.data ?: error("Empty body")
        }

    suspend fun sendText(sessionId: Int, content: String): Result<SendTextMessageResponseDto> =
        runCatching {
            val res = api.sendTextMessage(sessionId, SendTextMessageRequestDto(content))
            if (!res.isSuccessful) error(res.errorBody()?.string() ?: res.message())
            res.body()?.data ?: error("Empty body")
        }

    suspend fun sendVoice(sessionId: Int, audioFile: File, audioDurationSec: Int?): Result<SendTextMessageResponseDto> =
        runCatching {
            val mediaType = "audio/mp4".toMediaTypeOrNull()
            val body = audioFile.asRequestBody(mediaType)
            val part = MultipartBody.Part.createFormData("audio", audioFile.name, body)
            val durBody = audioDurationSec?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val res = api.sendVoiceMessage(sessionId, part, durBody)
            if (!res.isSuccessful) error(res.errorBody()?.string() ?: res.message())
            res.body()?.data ?: error("Empty body")
        }

    suspend fun endSession(sessionId: Int): Result<EndSessionResponseDto> =
        runCatching {
            val res = api.endSession(sessionId)
            if (!res.isSuccessful) error(res.errorBody()?.string() ?: res.message())
            res.body()?.data ?: error("Empty body")
        }

    suspend fun transcript(sessionId: Int): Result<List<ChatMessageDetailItemDto>> =
        runCatching {
            val res = api.sessionTranscript(sessionId)
            if (!res.isSuccessful) error(res.errorBody()?.string() ?: res.message())
            res.body()?.data ?: emptyList()
        }

    suspend fun history(): Result<List<ChatSessionHistoryItemDto>> =
        runCatching {
            val res = api.sessionHistory()
            if (!res.isSuccessful) error(res.errorBody()?.string() ?: res.message())
            res.body()?.data ?: emptyList()
        }

    suspend fun deleteSession(sessionId: Int): Result<Unit> =
        runCatching {
            val res = api.deleteSession(sessionId)
            if (!res.isSuccessful) error(res.errorBody()?.string() ?: res.message())
        }
}
