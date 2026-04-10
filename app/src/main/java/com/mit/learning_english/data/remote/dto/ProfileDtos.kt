package com.mit.learning_english.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProfileMeDto(
    @SerializedName(value = "userId", alternate = ["user_id"]) val userId: Int?,
    @SerializedName(value = "email") val email: String?,
    @SerializedName(value = "fullName", alternate = ["full_name"]) val fullName: String?,
    @SerializedName(value = "avatarUrl", alternate = ["avatar_url"]) val avatarUrl: String?,
    @SerializedName(value = "location") val location: String?,
    @SerializedName(value = "learningLevel", alternate = ["learning_level"]) val learningLevel: String?,
    @SerializedName(value = "levelId", alternate = ["level_id"]) val levelId: Int?,
    @SerializedName(value = "levelName", alternate = ["level_name"]) val levelName: String?,
    @SerializedName(value = "dailyGoalMinutes", alternate = ["daily_goal_minutes"]) val dailyGoalMinutes: Int?,
    @SerializedName(value = "learningGoal", alternate = ["learning_goal"]) val learningGoal: String?,
    @SerializedName(value = "jobTitle", alternate = ["job_title"]) val jobTitle: String?
)

data class ProfilePatchRequestDto(
    @SerializedName("fullName") val fullName: String? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("learningLevel") val learningLevel: String? = null
)

data class LearningStatsOverviewDto(
    @SerializedName(
        value = "completedLessonsOrExercises",
        alternate = ["completed_lessons_or_exercises"]
    )
    val completedLessonsOrExercises: Long = 0,
    @SerializedName(value = "wordsLearnedCount", alternate = ["words_learned_count"])
    val wordsLearnedCount: Long = 0,
    @SerializedName(value = "currentStreakDays", alternate = ["current_streak_days"])
    val currentStreakDays: Int = 0,
    @SerializedName(value = "totalStudyDays", alternate = ["total_study_days"])
    val totalStudyDays: Long = 0
)

data class HeatmapDayDto(
    @SerializedName("date") val date: String?,
    @SerializedName("intensity") val intensity: Int = 0,
    @SerializedName("totalMinutes") val totalMinutes: Int = 0,
    @SerializedName("activityCount") val activityCount: Long = 0
)

data class LearningActivityItemDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("activityType") val activityType: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("startedAt") val startedAt: String?,
    @SerializedName("endedAt") val endedAt: String?,
    @SerializedName("durationSeconds") val durationSeconds: Int?,
    @SerializedName("scorePercent") val scorePercent: java.math.BigDecimal?,
    @SerializedName("wordsNewCount") val wordsNewCount: Int?,
    @SerializedName("detailJson") val detailJson: String?,
    @SerializedName(value = "referenceType", alternate = ["reference_type"]) val referenceType: String?,
    @SerializedName(value = "referenceId", alternate = ["reference_id"]) val referenceId: Int?
)

data class ActivityDayDetailDto(
    @SerializedName("date") val date: String?,
    @SerializedName("totalMinutes") val totalMinutes: Int = 0,
    @SerializedName("activityCount") val activityCount: Long = 0,
    @SerializedName("averageScorePercent") val averageScorePercent: java.math.BigDecimal?,
    @SerializedName("dailyInsight") val dailyInsight: String?,
    @SerializedName("activities") val activities: List<LearningActivityItemDto>? = emptyList()
)

data class UserLearnedWordDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("term") val term: String?,
    @SerializedName("phonetic") val phonetic: String?,
    @SerializedName("definition") val definition: String?,
    @SerializedName("sourceModule") val sourceModule: String?,
    @SerializedName("favorite") val favorite: Boolean?,
    @SerializedName("needsAttention") val needsAttention: Boolean?,
    @SerializedName("audioUrl") val audioUrl: String?,
    @SerializedName("createdAt") val createdAt: String?
)

/** Spring Data Page JSON */
data class UserLearnedWordPatchDto(
    @SerializedName("favorite") val favorite: Boolean? = null,
    @SerializedName("needsAttention") val needsAttention: Boolean? = null
)

data class UserLearnedWordCreateDto(
    @SerializedName("term") val term: String,
    @SerializedName("sourceModule") val sourceModule: String? = "AI_CHAT"
)

data class UserCorrectionItemDto(
    @SerializedName("errorId") val errorId: Int?,
    @SerializedName(value = "errorType", alternate = ["error_type"]) val errorType: String?,
    @SerializedName(value = "originalText", alternate = ["original_text"]) val originalText: String?,
    @SerializedName(value = "suggestedText", alternate = ["suggested_text"]) val suggestedText: String?,
    @SerializedName("explanation") val explanation: String?,
    @SerializedName(value = "occurredAt", alternate = ["occurred_at"]) val occurredAt: String?,
    @SerializedName(value = "messageId", alternate = ["message_id"]) val messageId: Int?,
    @SerializedName(value = "sessionId", alternate = ["session_id"]) val sessionId: Int?,
    @SerializedName(value = "sessionTitle", alternate = ["session_title"]) val sessionTitle: String?,
    @SerializedName(value = "sourceLabel", alternate = ["source_label"]) val sourceLabel: String?
)

data class SessionCorrectionDetailDto(
    @SerializedName("errorId") val errorId: Int?,
    @SerializedName("category") val category: String?,
    @SerializedName(value = "originalText", alternate = ["original_text"]) val originalText: String?,
    @SerializedName(value = "suggestedText", alternate = ["suggested_text"]) val suggestedText: String?,
    @SerializedName("explanation") val explanation: String?
)

data class CorrectionSessionReviewDto(
    @SerializedName(value = "sessionId", alternate = ["session_id"]) val sessionId: Int?,
    @SerializedName(value = "contextHeader", alternate = ["context_header"]) val contextHeader: String?,
    @SerializedName(value = "sessionTitle", alternate = ["session_title"]) val sessionTitle: String?,
    @SerializedName(value = "sessionStartedAt", alternate = ["session_started_at"]) val sessionStartedAt: String?,
    @SerializedName(value = "sessionEndedAt", alternate = ["session_ended_at"]) val sessionEndedAt: String?,
    @SerializedName(value = "durationMinutes", alternate = ["duration_minutes"]) val durationMinutes: Int?,
    @SerializedName(value = "improvementCount", alternate = ["improvement_count"]) val improvementCount: Int?,
    @SerializedName("improvements") val improvements: List<SessionCorrectionDetailDto>? = emptyList()
)

data class SpringPageDto<T>(
    @SerializedName("content") val content: List<T> = emptyList(),
    @SerializedName("totalElements") val totalElements: Long = 0,
    @SerializedName("totalPages") val totalPages: Int = 0,
    @SerializedName("number") val number: Int = 0,
    @SerializedName("size") val size: Int = 0
)
