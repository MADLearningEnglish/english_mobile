package com.mit.learning_english.data.remote.dto

data class AiScenarioDto(
    val id: Int?,
    val title: String?,
    val description: String?,
    val topicId: Int?,
    val levelId: Int?,
    val levelName: String?,
    val type: String?,
    val aiRole: String?,
    val instruction: String?,
    val iconUrl: String?,
)

data class CreateChatSessionRequestDto(
    val userId: Int? = null,
    val scenarioId: Int? = null,
    val maxTurns: Int? = null,
)

data class CreateChatSessionResponseDto(
    val sessionId: Int?,
    val title: String?,
    val aiRole: String?,
    val instruction: String?,
    val status: String?,
    val currentTurn: Int?,
)

data class SendTextMessageRequestDto(
    val content: String,
)

data class ChatMessageItemDto(
    val id: Int?,
    val senderType: String?,
    val inputType: String?,
    val turnNumber: Int?,
    val content: String?,
    val createdAt: String?,
)

data class ErrorItemDto(
    val type: String?,
    val originalText: String?,
    val suggestedText: String?,
    val explanation: String?,
)

data class FeedbackDto(
    val pronunciationScore: Double?,
    val grammarScore: Double?,
    val vocabularyScore: Double?,
    val fluencyScore: Double?,
    val overallComment: String?,
    val improvedVersion: String?,
    val naturalSuggestion: String?,
    val errors: List<ErrorItemDto>?,
)

data class ChatMessageDetailItemDto(
    val id: Int?,
    val senderType: String?,
    val inputType: String?,
    val turnNumber: Int?,
    val content: String?,
    val createdAt: String?,
    val feedback: FeedbackDto?,
)

data class SendTextMessageResponseDto(
    val sessionId: Int?,
    val turnNumber: Int?,
    val userMessage: ChatMessageItemDto?,
    val aiMessage: ChatMessageItemDto?,
    val feedback: FeedbackDto?,
)

data class EndSessionResponseDto(
    val sessionId: Int?,
    val status: String?,
    val fluencyLevel: String?,
    val grammarLevel: String?,
    val vocabularyLevel: String?,
    val sentenceCount: Int?,
    val errorCount: Int?,
    val durationSeconds: Int?,
    val nextSuggestion: String?,
    val nextSuggestions: List<String>?,
)

data class ChatSessionHistoryItemDto(
    val sessionId: Int?,
    val title: String?,
    val status: String?,
    val startedAt: String?,
    val endedAt: String?,
    val currentTurn: Int?,
    val topicId: Int?,
    val levelId: Int?,
    val durationSeconds: Int?,
    val nextSuggestion: String?,
)
