package com.mit.learning_english.domain.model.profile

import java.math.BigDecimal
import java.time.LocalDate

data class ProfileMe(
    val userId: Int,
    val email: String,
    val fullName: String?,
    val avatarUrl: String?,
    val location: String?,
    val learningLevel: String?,
    val levelName: String?,
    val dailyGoalMinutes: Int?,
    val learningGoal: String?,
    val jobTitle: String?
)

data class LearningStatsOverview(
    val completedLessonsOrExercises: Long,
    val wordsLearnedCount: Long,
    val currentStreakDays: Int,
    val totalStudyDays: Long
)

data class HeatmapDay(
    val date: LocalDate,
    val intensity: Int,
    val totalMinutes: Int,
    val activityCount: Long
)

data class LearningActivityItem(
    val id: Int?,
    val activityType: String?,
    val title: String?,
    val durationSeconds: Int?,
    val scorePercent: BigDecimal?,
    val wordsNewCount: Int?,
    val detailJson: String?,
    val referenceType: String? = null,
    val referenceId: Int? = null,
    /** ISO-8601 từ API (vd. 2023-10-12T14:30:00) */
    val startedAt: String? = null
)

data class ActivityDayDetail(
    val date: LocalDate,
    val totalMinutes: Int,
    val activityCount: Long,
    val averageScorePercent: BigDecimal?,
    val dailyInsight: String?,
    val activities: List<LearningActivityItem>
)

data class VocabularyWord(
    val id: Int,
    val term: String,
    val phonetic: String?,
    val definition: String?,
    val favorite: Boolean,
    val needsAttention: Boolean,
    val audioUrl: String?
)

data class UserCorrectionItem(
    val errorId: Int,
    val errorType: String?,
    val originalText: String?,
    val suggestedText: String?,
    val explanation: String?,
    val occurredAt: String?,
    val messageId: Int,
    val sessionId: Int,
    val sessionTitle: String?,
    val sourceLabel: String?
)

data class SessionCorrectionDetail(
    val errorId: Int,
    val category: String?,
    val originalText: String?,
    val suggestedText: String?,
    val explanation: String?
)

data class CorrectionSessionReview(
    val sessionId: Int,
    val contextHeader: String?,
    val sessionTitle: String?,
    val sessionStartedAt: String?,
    val sessionEndedAt: String?,
    val durationMinutes: Int?,
    val improvementCount: Int,
    val improvements: List<SessionCorrectionDetail>
)
