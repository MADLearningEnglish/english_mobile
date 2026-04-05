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
    val detailJson: String?
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
